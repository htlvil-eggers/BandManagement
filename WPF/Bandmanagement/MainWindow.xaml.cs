using Bandmanagement.Model;
using Bandmanagement.View;
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

namespace Bandmanagement
{
    /// <summary>
    /// Interaktionslogik für MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
        }

        private void rbLogin_Changed(object sender, RoutedEventArgs e)
        {
            //switch the shown form
            if (this.IsInitialized == true)
            {
                RadioButton checkedRadioButton = this.grLogin.Children.OfType<RadioButton>().FirstOrDefault(r => r.IsChecked == true);

                if (checkedRadioButton.Name == "rbSignIn")
                {
                    this.grSignIn.Visibility = Visibility.Visible;
                    this.grCreateBand.Visibility = Visibility.Hidden;
                }
                else
                {
                    this.grSignIn.Visibility = Visibility.Hidden;
                    this.grCreateBand.Visibility = Visibility.Visible;
                }
            }
        }

        private void btnSignIn_Click(object sender, RoutedEventArgs e)
        {
            //check data (if band exists and so) and try to login or print error
            Musician insertedMusician = new Musician();
            insertedMusician.Username = this.tbUsernameSignIn.Text;
            insertedMusician.Password = this.pbPasswordSignIn.Password;

            Band insertedBand = new Band();
            insertedBand.Name = this.tbBandnameSignIn.Text;
            insertedBand.Leader = insertedMusician;

            if (insertedBand.Name.Length > 0 && insertedMusician.Username.Length > 0 && insertedMusician.Password.Length > 0)  //if data is correct
            {
                
                insertedBand = WebserviceManager.GetBandOfLeader(insertedBand.Name, insertedMusician.Username);

                if ((insertedMusician = WebserviceManager.GetMusician(insertedMusician.Username, insertedMusician.Password)) != null && insertedMusician.IsDataCorrect() == true)  //if data is correct: log in succeeded
                {
                    insertedBand.Leader = insertedMusician;
                    BandmemberManagement managementWindow = new BandmemberManagement(insertedBand);
                    managementWindow.Show();
                }
                else
                {
                    this.printError("Falsche Login-Daten!");
                }
            }

            else
            {
                this.printError("Alle Felder müssen ausgefüllt werden!");
            }
        }


        private void btnCreateBand_Click(object sender, RoutedEventArgs e)
        {
            //check data and if correct: save new band to sql database
            Musician insertedMusician = new Musician(0, this.tbUsernameCreateBand.Text, this.pbPasswordCreateBand.Password,
                                                     this.tbFirstNameCreateBand.Text, this.tbLastNameCreateBand.Text);
            Band insertedBand = new Band(0, this.tbBandnameCreateBand.Text, insertedMusician);

            if (insertedBand.IsDataCorrect() == true && insertedMusician.IsDataCorrect() == true)
            {
                //eventuell überprüfen ob login auf Musician erfolgreich -> keinen neuen erstellen!

                WebserviceManager.AddMusician(insertedMusician);
                WebserviceManager.AddBand(insertedBand, insertedMusician.Username);

                //set insertedBand id
                insertedBand.Id = WebserviceManager.GetIdFromBandname(insertedBand.Name);

                BandmemberManagement managementWindow = new BandmemberManagement(insertedBand);
                managementWindow.Show();
            }

            else
            {
                this.printError("Alle Felder müssen ausgefüllt werden!");
            }
        }

        private void printError(String error)
        {
            this.lblError.Content = error;
        }
    }
}
