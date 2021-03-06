﻿using Bandmanagement.Model;
using SpatialClient.Model;
using System;
using System.Collections.Generic;
using System.Data.OleDb;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace SpatialClient
{
    /// <summary>
    /// Interaktionslogik für MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private EnumSpatialType currentSpatialInsertType = EnumSpatialType.CITY;  //default value as it's selected at the start
        private Shape currentSpatialObjectSelected = null;  
        Boolean IsDragAndDropActive = false;
        Boolean IsDrawingStreet = false;
        Point StreetStartPoint = new Point();
        Line DrawingLine = new Line();
        Point StreetDragDropStart = new Point();

        public MainWindow()
        {
            InitializeComponent();

            fillCanSpatialDataFromDb();  //load all spatial objects and display it on the canvas
        }

        private void rbgInputMode_changed(object sender, RoutedEventArgs e)  //changed input method (city or street)
        {
            if (this.IsInitialized == true)
            {
                RadioButton checkedRadioButton = this.grInputMode.Children.OfType<RadioButton>().FirstOrDefault(r => r.IsChecked == true);

                if (checkedRadioButton.Name == "rbInputModeCity")
                {
                    this.currentSpatialInsertType = EnumSpatialType.CITY;
                }
                else
                {
                    this.currentSpatialInsertType = EnumSpatialType.STREET;
                }
            }
        }


        #region canvasEvents
        private void canSpatialData_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)  //if mouse on canvas pressed
        {
            Point currentMousePos = e.GetPosition(this.canSpatialData);
            Shape clickedElement = this.canSpatialData.InputHitTest(currentMousePos) as Shape;

            if (clickedElement != null && clickedElement == this.currentSpatialObjectSelected)
            {
                this.IsDragAndDropActive = true;
                if (this.currentSpatialObjectSelected is Line) //if is street
                {
                    this.StreetDragDropStart = currentMousePos;
                }
            }
            else
            {
                //insert city or street
                if (this.currentSpatialInsertType == EnumSpatialType.CITY)  //insert city at mouse position
                {
                    Location locToInsert = new Location();
                    locToInsert.Name = this.tbNameOfObject.Text;

                    //insert city in oracle
                    locToInsert.Id = WebserviceManager.AddLocation(locToInsert, Math.Round(currentMousePos.X, 2), Math.Round(currentMousePos.Y, 2));

                    //insert city in canvas
                    String ellName = "city_" + locToInsert.Id + "_" + locToInsert.Name;  //name like "city_1_Villach
                    Ellipse ellCity = generateEllipse(ellName, 12, 12);

                    this.canSpatialData.Children.Add(ellCity);
                    Canvas.SetTop(ellCity, Math.Round(currentMousePos.Y, 2) - (ellCity.Height / 2));
                    Canvas.SetLeft(ellCity, Math.Round(currentMousePos.X, 2) - (ellCity.Width / 2));
                }
                else if (this.currentSpatialInsertType == EnumSpatialType.STREET) //set start point of the street
                {
                    this.StreetStartPoint = currentMousePos;
                    PointCollection pC = new PointCollection();
                    pC.Add(this.StreetStartPoint);
                    pC.Add(this.StreetStartPoint);
                    this.IsDrawingStreet = true;
                    this.DrawingLine = generateLine("drawingLine", pC);
                    this.canSpatialData.Children.Add(this.DrawingLine);
                }
            }          
        }

        private void canSpatialData_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            Point currentMousePos = e.GetPosition(this.canSpatialData);

            if (this.IsDragAndDropActive == true)
            {
                if (this.currentSpatialObjectSelected is Ellipse) //is city
                {
                    Location locToUpdate = this.getLocationDataFromEllName(this.currentSpatialObjectSelected.Name);

                    WebserviceManager.UpdateLocation(locToUpdate, Math.Round(currentMousePos.X, 2), Math.Round(currentMousePos.Y, 2));
                }
                else if (this.currentSpatialObjectSelected is Line) //is street
                {
                    Street locToUpdate = this.getStreetDataFromEllName(this.currentSpatialObjectSelected.Name);

                    WebserviceManager.UpdateStreet(locToUpdate, Math.Round(((Line)this.currentSpatialObjectSelected).X1, 2), Math.Round(((Line)this.currentSpatialObjectSelected).Y1, 2),
                        Math.Round(((Line)this.currentSpatialObjectSelected).X2, 2), Math.Round(((Line)this.currentSpatialObjectSelected).Y2, 2));
                }

                this.IsDragAndDropActive = false;
            }
            else
            {
                if (this.currentSpatialInsertType == EnumSpatialType.STREET) //set end point of street and insert street to db
                {
                    Point streetEndPoint = currentMousePos;
                    Street insertedStr = new Street();
                    insertedStr.Name = this.tbNameOfObject.Text;

                    insertedStr.Id = WebserviceManager.AddStreet(insertedStr, Math.Round(this.StreetStartPoint.X, 2), Math.Round(this.StreetStartPoint.Y, 2),
                        Math.Round(streetEndPoint.X, 2), Math.Round(streetEndPoint.Y, 2));

                    setEllNameFromStreetData(insertedStr, this.DrawingLine);
                    this.IsDrawingStreet = false;
                }
            }
        }


        private void canSpatialData_MouseRightButtonDown(object sender, MouseButtonEventArgs e)
        {
            Point currentMousePos = e.GetPosition(this.canSpatialData);
            Shape clickedElement = this.canSpatialData.InputHitTest(currentMousePos) as Shape;

            if (clickedElement != null) //if mouse is over an shape (spatial object) -> select it to update data
            {
                if (this.currentSpatialObjectSelected != null)  //remove color-change of previous selected
                {
                    if (this.currentSpatialObjectSelected is Ellipse) //so if it is a city
                    {
                        this.currentSpatialObjectSelected.Fill = Brushes.LightBlue;
                    }
                    else if (this.currentSpatialObjectSelected is Line) //street
                    {
                        this.currentSpatialObjectSelected.Stroke = Brushes.DarkGray;
                    }
                }

                this.currentSpatialObjectSelected = clickedElement;

                if(this.currentSpatialObjectSelected is Ellipse) //so if it is a city
                {
                    Location locData = getLocationDataFromEllName(this.currentSpatialObjectSelected.Name);
                    this.tbNameOfObject.Text = locData.Name;

                    this.currentSpatialObjectSelected.Fill = Brushes.Red;
                }
                else if (this.currentSpatialObjectSelected is Line) //street
                {
                    Street streetData = getStreetDataFromEllName(this.currentSpatialObjectSelected.Name);
                    this.tbNameOfObject.Text = streetData.Name;

                    this.currentSpatialObjectSelected.Stroke = Brushes.Red;
                }
            }
        }

        private void canSpatialData_MouseMove(object sender, MouseEventArgs e)
        {
            this.popMouseInCanvas.IsOpen = true;

            Point currentMousePos = e.GetPosition(this.canSpatialData);
            Shape clickedElement = this.canSpatialData.InputHitTest(currentMousePos) as Shape;

            if (clickedElement != null) //if mouse is over an shape (spatial object)
            {
                if (clickedElement == this.currentSpatialObjectSelected)
                {
                    Mouse.OverrideCursor = Cursors.Hand;
                }

                if (clickedElement is Ellipse)  //if over city
                {
                    Location locData = getLocationDataFromEllName(clickedElement.Name);

                    //display city name in mouse tooltip
                    this.popMouseInCanvas.HorizontalOffset = currentMousePos.X + 20;
                    this.popMouseInCanvas.VerticalOffset = currentMousePos.Y;

                    this.tbPopMouseInCanvas.Text = "City: " + locData.Name + "\nX: " + Math.Round(currentMousePos.X, 2) + ", Y: " + Math.Round(currentMousePos.Y, 2);
                }
                else if (clickedElement is Line) // street
                {
                    if (this.IsDrawingStreet == false)
                    {
                        Street locData = getStreetDataFromEllName(clickedElement.Name);

                        //display city name in mouse tooltip
                        this.popMouseInCanvas.HorizontalOffset = currentMousePos.X + 20;
                        this.popMouseInCanvas.VerticalOffset = currentMousePos.Y;

                        this.tbPopMouseInCanvas.Text = "Street: " + locData.Name + "\nX: " + Math.Round(currentMousePos.X, 2) + ", Y: " + Math.Round(currentMousePos.Y, 2);
                    }
                }
            }
            else
            {
                Mouse.OverrideCursor = Cursors.Arrow;

                // display mouse-tooltip with current coordinates
                this.popMouseInCanvas.HorizontalOffset = currentMousePos.X + 20;
                this.popMouseInCanvas.VerticalOffset = currentMousePos.Y;

                this.tbPopMouseInCanvas.Text = "X: " + Math.Round(currentMousePos.X, 2) + ", Y: " + Math.Round(currentMousePos.Y, 2);
            }

            if (this.IsDragAndDropActive == true)
            {
                if (this.currentSpatialObjectSelected is Ellipse) //is city
                {
                    Canvas.SetTop(this.currentSpatialObjectSelected, Math.Round(currentMousePos.Y, 2) - (this.currentSpatialObjectSelected.Height / 2));
                    Canvas.SetLeft(this.currentSpatialObjectSelected, Math.Round(currentMousePos.X, 2) - (this.currentSpatialObjectSelected.Width / 2));
                }
                else if (this.currentSpatialObjectSelected is Line) //is street
                {
                    ((Line)this.currentSpatialObjectSelected).X1 += (currentMousePos.X - this.StreetDragDropStart.X);
                    ((Line)this.currentSpatialObjectSelected).Y1 += (currentMousePos.Y - this.StreetDragDropStart.Y);
                    ((Line)this.currentSpatialObjectSelected).X2 += (currentMousePos.X - this.StreetDragDropStart.X);
                    ((Line)this.currentSpatialObjectSelected).Y2 += (currentMousePos.Y - this.StreetDragDropStart.Y);

                    this.StreetDragDropStart = currentMousePos;
                }
            }

            if (this.IsDrawingStreet == true)
            {
                this.DrawingLine.X2 = currentMousePos.X;
                this.DrawingLine.Y2 = currentMousePos.Y;
            }
        }

        private void canSpatialData_MouseLeave(object sender, MouseEventArgs e)
        {
            this.popMouseInCanvas.IsOpen = false;
        }

        private void fillCanSpatialDataFromDb()
        {
            String ellName = null;
            Ellipse nextLocation = null;
            Line nextStreet = null;
            Dictionary<String, PointCollection> streetData = new Dictionary<String, PointCollection>();  //string-Key is like id + _ + name e.g.: "1_Bundesstraße Villach"

            List<AddSpatialWrapper> wrappers = WebserviceManager.GetLocationsSpatial();

            foreach (AddSpatialWrapper wrapper in wrappers)
            {
                ellName = "city_" + wrapper.Location.Id + "_" + wrapper.Location.Name;  //name like "city_1_Villach
                nextLocation = generateEllipse(ellName, 12, 12);
                
                this.canSpatialData.Children.Add(nextLocation);
                Canvas.SetTop(nextLocation, wrapper.Y - (nextLocation.Height / 2));
                Canvas.SetLeft(nextLocation, wrapper.X - (nextLocation.Width / 2));
            }

            //read streets
            wrappers = WebserviceManager.GetStreetsSpatial();  //TODO

            foreach (AddSpatialWrapper wrapper in wrappers)
            {
                String nextKey = wrapper.Street.Id + "_" + wrapper.Street.Name;

                if (streetData.ContainsKey(nextKey) == false)
                {
                    streetData.Add(nextKey, new PointCollection());
                }

                streetData[nextKey].Add(new Point(Math.Round(wrapper.X, 2), Math.Round(wrapper.Y, 2)));
            }

            foreach (String key in streetData.Keys)
            {
                ellName = "street_" + key;
                nextStreet = generateLine(ellName, streetData[key]);

                this.canSpatialData.Children.Add(nextStreet);
            }
        }
        #endregion


        private void btnUpdateSelectedObject_Click(object sender, RoutedEventArgs e)
        {
            if (this.currentSpatialObjectSelected != null)
            {
                if (this.currentSpatialObjectSelected is Ellipse) //if its a city
                {
                    Location locToUpdate = getLocationDataFromEllName(this.currentSpatialObjectSelected.Name);
                    locToUpdate.Name = this.tbNameOfObject.Text;

                    //update in oracle
                    WebserviceManager.UpdateLocation(locToUpdate, -1, -1);
                    setEllNameFromLocationData(locToUpdate, (Ellipse)this.currentSpatialObjectSelected);
                }
                else if (this.currentSpatialObjectSelected is Line) //if street
                {
                    Street locToUpdate = getStreetDataFromEllName(this.currentSpatialObjectSelected.Name);
                    locToUpdate.Name = this.tbNameOfObject.Text;

                    //update in oracle
                    WebserviceManager.UpdateStreet(locToUpdate, -1, -1, -1, -1);

                    setEllNameFromStreetData(locToUpdate, (Line)this.currentSpatialObjectSelected);
                }
            }
            else
            {
                this.printError("Keinen Ort bzw. keine Straße ausgewählt!");
            }
        }

        private void btnDeleteSelectedObject_Click(object sender, RoutedEventArgs e)
        {
            if (this.currentSpatialObjectSelected != null)
            {
                if (this.currentSpatialObjectSelected is Ellipse) //if is city
                {
                    Location locToDelete = getLocationDataFromEllName(this.currentSpatialObjectSelected.Name);

                    //delete in oracle
                    WebserviceManager.DeleteLocation(locToDelete.Id);
                }
                else if (this.currentSpatialObjectSelected is Line) //if is street
                {
                    Street locToDelete = getStreetDataFromEllName(this.currentSpatialObjectSelected.Name);

                    //delete in oracle
                    WebserviceManager.DeleteStreet(locToDelete.Id);
                }

                //delte in canvas
                this.canSpatialData.Children.Remove(this.currentSpatialObjectSelected);
                this.currentSpatialObjectSelected = null;
            }
            else
            {
                this.printError("Keinen Ort bzw. keine Straße ausgewählt!");
            }
        }



        private void printError(String errorText)
        {
            this.lblError.Content = errorText;
        }

        private Ellipse generateEllipse(String name, double height, double width)
        {
            Ellipse generatedEllipse = new Ellipse();
            generatedEllipse.Name = name;
            generatedEllipse.Height = height;
            generatedEllipse.Width = width;
            generatedEllipse.StrokeThickness = 2;
            generatedEllipse.HorizontalAlignment = HorizontalAlignment.Center;
            generatedEllipse.VerticalAlignment = VerticalAlignment.Center;
            generatedEllipse.Stroke = Brushes.Black;
            generatedEllipse.Fill = Brushes.LightBlue;

            return generatedEllipse;
        }

        private Line generateLine(String name, PointCollection pC)
        {
            Line generatedLine = new Line();
            generatedLine.Name = name;
            generatedLine.X1 = pC.ElementAt(0).X;
            generatedLine.Y1 = pC.ElementAt(0).Y;
            generatedLine.X2 = pC.ElementAt(1).X;
            generatedLine.Y2 = pC.ElementAt(1).Y;
            generatedLine.StrokeThickness = 6;
            generatedLine.HorizontalAlignment = HorizontalAlignment.Center;
            generatedLine.VerticalAlignment = VerticalAlignment.Center;
            generatedLine.Stroke = Brushes.DarkGray;

            return generatedLine;
        }

        private Location getLocationDataFromEllName(String ellName)
        {
            Location retLoc = new Location();
            String[] splittedEllName = ellName.Split('_');
            int parsedId;

            int.TryParse(splittedEllName[1], out parsedId);
            retLoc.Id = parsedId;
            retLoc.Name = splittedEllName[2];

            return retLoc;
        }

        private Street getStreetDataFromEllName(String lineName)
        {
            Street retStreet = new Street();
            String[] splittedEllName = lineName.Split('_');
            int parsedId;

            int.TryParse(splittedEllName[1], out parsedId);
            retStreet.Id = parsedId;
            retStreet.Name = splittedEllName[2];

            return retStreet;
        }

        private void setEllNameFromLocationData(Location locData, Ellipse ell)  //sets name of ellipse corresponding to the data of the location
        {
            ell.Name = "city_" + locData.Id + "_" + locData.Name;
        }

        private void setEllNameFromStreetData(Street locData, Line ell)  //sets name of ellipse corresponding to the data of the location
        {
            ell.Name = "street_" + locData.Id + "_" + locData.Name;
        }

        private void canSpatialData_MouseWheel(object sender, MouseWheelEventArgs e)
        {
            if (e.Delta > 0)
            {
                this.sTCanSpatialData.ScaleX *= 1.1;
                this.sTCanSpatialData.ScaleY *= 1.1;
            }
            else
            {
                this.sTCanSpatialData.ScaleX /= 1.1;
                this.sTCanSpatialData.ScaleY /= 1.1;
            }
        }
    }
}
