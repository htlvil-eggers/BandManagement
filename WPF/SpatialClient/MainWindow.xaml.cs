using SpatialClient.Model;
using System;
using System.Collections.Generic;
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
        private EnumSpatialType currentSpatialInsertType = EnumSpatialType.CITY;

        public MainWindow()
        {
            InitializeComponent();
        }

        private void rbgInputMode_changed(object sender, RoutedEventArgs e)
        {
            //switch the shown form
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

        private void canSpatialData_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)  //if canvas pressed
        {
            if (this.currentSpatialInsertType == EnumSpatialType.CITY)
            {

            }
        }
    }
}
