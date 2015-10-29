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
        private DatabaseOracle dbOra = new DatabaseOracle();

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
                dbOra.Connect();


                OleDbCommand cmdSelectData = dbOra.getMyOleDbConnection().CreateCommand();
                cmdSelectData.CommandText = @"select b.id, mus.id, mus.first_name, mus.last_name from Musicians mus join Bands b on mus.id = b.leader_id 
                                         where b.bandname = ? and mus.username = ? and mus.password = ?";
                cmdSelectData.Parameters.AddWithValue("bandname", insertedBand.Name);
                cmdSelectData.Parameters.AddWithValue("username", insertedMusician.Username);
                cmdSelectData.Parameters.AddWithValue("password", insertedMusician.Password);

                OleDbDataReader reader = cmdSelectData.ExecuteReader();
                while (reader.Read())  //set data if sth found
                {
                    insertedBand.Id = Int32.Parse(reader["b.id"].ToString());
                    insertedMusician.Id = Int32.Parse(reader["mus.id"].ToString());
                    insertedMusician.FirstName = reader["mus.first_name"].ToString();
                    insertedMusician.LastName = reader["mus.last_name"].ToString();
                }

                if (insertedMusician.IsDataCorrect() == true)  //if data is correct: log in succeeded
                {
                    BandmemberManagement managementWindow = new BandmemberManagement(insertedBand);
                    managementWindow.Show();
                }

                dbOra.Close();
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
            String readPassword = null;

            if (insertedBand.IsDataCorrect() == true && insertedMusician.IsDataCorrect() == true)
            {
                dbOra.Connect();

                OleDbCommand cmdInsertMusician = dbOra.getMyOleDbConnection().CreateCommand();
                cmdInsertMusician.CommandText = "insert into musicians values (0, ?, ?, ?, ?, null, null)";
                cmdInsertMusician.Parameters.AddWithValue("username", insertedMusician.Username);
                cmdInsertMusician.Parameters.AddWithValue("password", insertedMusician.Password);
                cmdInsertMusician.Parameters.AddWithValue("firstname", insertedMusician.FirstName);
                cmdInsertMusician.Parameters.AddWithValue("lastname", insertedMusician.LastName);

                OleDbCommand cmdSelectMusicianData = dbOra.getMyOleDbConnection().CreateCommand();
                cmdSelectMusicianData.CommandText = "select id, password from musicians where username = ?";
                cmdSelectMusicianData.Parameters.AddWithValue("username", insertedMusician.Username);


                OleDbCommand cmdInsertBand = dbOra.getMyOleDbConnection().CreateCommand();
                cmdInsertBand.CommandText = "insert into bands values (0, ?, ?, null)";
                cmdInsertBand.Parameters.AddWithValue("bandname", insertedBand.Name);


                OleDbTransaction transInsertBand = dbOra.getMyOleDbConnection().BeginTransaction(System.Data.IsolationLevel.Serializable);
                cmdInsertMusician.Transaction = transInsertBand;
                cmdSelectMusicianData.Transaction = transInsertBand;
                cmdInsertBand.Transaction = transInsertBand;

                try
                {
                    OleDbDataReader reader = cmdSelectMusicianData.ExecuteReader(); //get password if musician already exists
                    while (reader.Read())
                    {
                        insertedMusician.Id = Int32.Parse(reader["id"].ToString());
                        readPassword = reader["password"].ToString();
                    }
                    reader.Close();

                    if (readPassword != insertedMusician.Password) //if musician doesnt exist or credentials are wrong
                    {
                        cmdInsertMusician.ExecuteNonQuery();  //insert musician
                        
                        reader = cmdSelectMusicianData.ExecuteReader(); //get id of inserted musician
                        while (reader.Read())
                        {
                            insertedMusician.Id = Int32.Parse(reader["id"].ToString());
                        }
                    }                   

                    cmdInsertBand.Parameters.AddWithValue("musicianId", insertedMusician.Id);   //insert band
                    cmdInsertBand.ExecuteNonQuery();

                    transInsertBand.Commit();
                    this.lblError.Content = "Band erstellt!";

                    BandmemberManagement managementWindow = new BandmemberManagement(insertedBand);
                    managementWindow.Show();
                }
                catch (Exception _e)
                {
                    transInsertBand.Rollback();
                    this.lblError.Content = "Username oder Bandname existieren bereits!";
                }

                dbOra.Close();
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
