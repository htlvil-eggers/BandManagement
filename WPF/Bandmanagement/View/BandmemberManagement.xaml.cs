using Bandmanagement.Model;
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
using System.Windows.Shapes;
using Xceed.Wpf.Toolkit;
using System.Collections;

namespace Bandmanagement.View
{
    /// <summary> 
    /// Interaktionslogik für BandmemberManagement.xaml
    /// </summary>
    public partial class BandmemberManagement : Window
    {
        public Band currentBand;  //TODO: rehearsal request (probe zwischen 2 daten mit stundenanzahl)
        public DatabaseOracle dbOra;

        public BandmemberManagement(DatabaseOracle connectedDb, Band signedInBand)
        {
            InitializeComponent();

            dbOra = connectedDb;
            currentBand = signedInBand;

            //set datagrids
            this.currentBand.Musicians = selectMembersOfBand(currentBand);
            this.dgCurrentBandmembers.ItemsSource = this.currentBand.Musicians;

            this.currentBand.AppearanceRequests = selectAppearanceRequestOfBand(currentBand);
            this.dgReceivedAppearanceRequests.ItemsSource = this.currentBand.AppearanceRequests;

            this.currentBand.Appointments = selectAppointmentsOfBand(currentBand);
            this.dgAppointments.ItemsSource = this.currentBand.Appointments;

            this.currentBand.RehearsalRequests = selectRehearsalRequestsOfBand(currentBand);
            this.dgRehearsalRequests.ItemsSource = this.currentBand.RehearsalRequests;

            DisplaySignedInData();
        }

        private void DisplaySignedInData()
        {
            this.lblDisplaySignedInUsername.Content = this.currentBand.Leader.Username;
            this.lblDisplaySignedInBand.Content = this.currentBand.Name;
        }

        private void btnAddBandmember_Click(object sender, RoutedEventArgs e)
        {
            //check data (if band exists and so) and try to login or print error
            Musician insertedMusician = new Musician();
            insertedMusician.Username = this.tbAddBandmemberUsername.Text;
            insertedMusician.Password = this.pbAddBandmeberPassword.Password;

            if (insertedMusician.Username.Length > 0 && insertedMusician.Password.Length > 0)  //if data is correct
            {
                //add bandmeber to band
                dbOra.Connect();

                //insert musician
                OleDbCommand cmdInsertMusician = dbOra.getMyOleDbConnection().CreateCommand();
                cmdInsertMusician.CommandText = "insert into musicians values (0, ?, ?, ?, ?, null, null)";
                cmdInsertMusician.Parameters.AddWithValue("username", insertedMusician.Username);
                cmdInsertMusician.Parameters.AddWithValue("password", insertedMusician.Password);
                cmdInsertMusician.Parameters.AddWithValue("firstname", insertedMusician.FirstName);
                cmdInsertMusician.Parameters.AddWithValue("lastname", insertedMusician.LastName);

                try
                {
                    cmdInsertMusician.ExecuteNonQuery();

                    //select inserted-musician id
                    insertedMusician = selectDataFromMusicianUsername(insertedMusician);

                    //insert bandmember
                    insertBandmember(currentBand, insertedMusician);
                }
                catch (Exception)
                {
                    if (currentBand.Musicians.FirstOrDefault(bm => bm.Username == insertedMusician.Username) != null) //if musician is in this band
                    {
                        this.printError("Musiker bereits in eigener Band!");
                    }

                    else if (Xceed.Wpf.Toolkit.MessageBox.Show("User existiert bereits", 
                        "Möchten sie den User trotzdem zur Band hinzufügen?", MessageBoxButton.YesNo, MessageBoxImage.Question) == MessageBoxResult.Yes)
                    {
                        //insert Bandmember
                        insertedMusician = selectDataFromMusicianUsername(insertedMusician);

                        insertBandmember(currentBand, insertedMusician);
                    }
                }
            }
            else
            {
                this.printError("Alle Felder müssen ausgefüllt werden!");
            }

            this.dbOra.Close();
        }

        private void insertBandmember(Band band, Musician insertedMusician)
        {
            OleDbCommand cmdInsertBandmember = dbOra.getMyOleDbConnection().CreateCommand();
            cmdInsertBandmember.CommandText = "insert into bandmembers values(?, ?, ?)";
            cmdInsertBandmember.Parameters.AddWithValue("bandId", band.Id);
            cmdInsertBandmember.Parameters.AddWithValue("musicianId", insertedMusician.Id);
            cmdInsertBandmember.Parameters.AddWithValue("joinedDate", DateTime.Today.ToString("d"));

            cmdInsertBandmember.ExecuteNonQuery();

            this.currentBand.Musicians.Add(insertedMusician);
            this.dgCurrentBandmembers.Items.Refresh();
        }

        private Musician selectDataFromMusicianUsername(Musician insertedMusician)
        {
            Musician retMusician = insertedMusician;

            OleDbCommand cmdSelectMusicianData = dbOra.getMyOleDbConnection().CreateCommand();
            cmdSelectMusicianData.CommandText = "select id, password, first_name, last_name from musicians where username = ?";
            cmdSelectMusicianData.Parameters.AddWithValue("username", insertedMusician.Username);

            OleDbDataReader reader = cmdSelectMusicianData.ExecuteReader(); //get password if musician already exists
            while (reader.Read())
            {
                retMusician.Id = Int32.Parse(reader["id"].ToString());
                retMusician.Password = reader["password"].ToString();
                retMusician.FirstName = reader["first_name"].ToString();
                retMusician.LastName = reader["last_name"].ToString();
            }
            reader.Close();

            return retMusician;
        }

        private List<Musician> selectMembersOfBand(Band band)
        {
            List<Musician> retList = new List<Musician>();

            this.dbOra.Connect();

            OleDbCommand cmdSelectMusicianData = dbOra.getMyOleDbConnection().CreateCommand();
            cmdSelectMusicianData.CommandText = @"select m.id as mId, m.username as mUsername, m.password as mPwd, m.first_name as mFName,
                m.last_name mLName, m.birthdate mBirth from musicians m 
                join bandmembers bm on m.id = bm.musician_id where bm.band_id = ?";
            cmdSelectMusicianData.Parameters.AddWithValue("bandId", band.Id);

            OleDbDataReader reader = cmdSelectMusicianData.ExecuteReader(); //get password if musician already exists
            while (reader.Read())
            {
                Musician next = new Musician();
                next.Id = int.Parse(reader["mId"].ToString());
                next.Username = reader["mUsername"].ToString();
                next.Password = reader["mPwd"].ToString();
                next.FirstName = reader["mFName"].ToString();
                next.LastName = reader["mLName"].ToString();

                retList.Add(next);
            }

            reader.Close();
            this.dbOra.Close();

            return retList;
        }

        private List<AppearanceRequest> selectAppearanceRequestOfBand(Band band)
        {
            List<AppearanceRequest> retRequests = new List<AppearanceRequest>();
            this.dbOra.Connect();

            OleDbCommand cmdSelectMusicianData = dbOra.getMyOleDbConnection().CreateCommand();
            cmdSelectMusicianData.CommandText = @"select m.id as mId, m.start_time as mSTime, m.end_time as mETime, l.id as lId, l.name as lName,
                m.accepted mAccepted, m.Name as mName, m.Description as mDesc from appearance_requests m join locations l on m.location_id = l.id where m.band_id = ?";
            cmdSelectMusicianData.Parameters.AddWithValue("bandId", band.Id);

            OleDbDataReader reader = cmdSelectMusicianData.ExecuteReader(); //get password if musician already exists
            while (reader.Read())
            {
                AppearanceRequest next = new AppearanceRequest();
                next.Id = int.Parse(reader["mId"].ToString());
                next.Name = reader["mName"].ToString();
                next.Description = reader["mDesc"].ToString();
                next.StartTime = DateTime.Parse(reader["mSTime"].ToString());
                next.EndTime = DateTime.Parse(reader["mETime"].ToString());
                next.Location.Id = int.Parse(reader["lId"].ToString());
                next.Location.Name = reader["lName"].ToString();
                next.Accepted = int.Parse(reader["mAccepted"].ToString());

                retRequests.Add(next);
            }

            reader.Close();
            this.dbOra.Close();
            return retRequests;
        }

        private List<RehearsalRequest> selectRehearsalRequestsOfBand(Band band)
        {
            List<RehearsalRequest> retRequests = new List<RehearsalRequest>();
            this.dbOra.Connect();

            OleDbCommand cmdSelectRequests = dbOra.getMyOleDbConnection().CreateCommand();
            cmdSelectRequests.CommandText = @"select m.id as mId, m.start_time as mSTime, m.end_time as mETime,  m.duration as mDuration from rehearsal_requests m where m.band_id = ?";
            cmdSelectRequests.Parameters.AddWithValue("bandId", band.Id);

            OleDbDataReader reader = cmdSelectRequests.ExecuteReader();
            while (reader.Read())
            {
                RehearsalRequest next = new RehearsalRequest();
                next.Id = int.Parse(reader["mId"].ToString());
                next.StartTime = DateTime.Parse(reader["mSTime"].ToString());
                next.EndTime = DateTime.Parse(reader["mETime"].ToString());
                next.Duration = Double.Parse(reader["mDuration"].ToString());

                retRequests.Add(next);
            }

            reader.Close();
            this.dbOra.Close();

            return retRequests;
        }

        private List<Appointment> selectAppointmentsOfBand(Band band)
        {
            List<Appointment> retAppointments = new List<Appointment>();
            this.dbOra.Connect();

            OleDbCommand cmdSelectMusicianData = dbOra.getMyOleDbConnection().CreateCommand();
            cmdSelectMusicianData.CommandText = @"select m.id as mId, m.start_time as mSTime, m.end_time as mETime, l.id as lId, l.name as lName,
                m.grounded mGrounded, m.Name as mName, m.Description as mDesc from appointments m join locations l on m.location_id = l.id where m.band_id = ?";
            cmdSelectMusicianData.Parameters.AddWithValue("bandId", band.Id);

            OleDbDataReader reader = cmdSelectMusicianData.ExecuteReader(); //get password if musician already exists
            while (reader.Read())
            {
                Appointment next = new Appointment();
                next.Id = int.Parse(reader["mId"].ToString());
                next.Name = reader["mName"].ToString();
                next.Description = reader["mDesc"].ToString();
                next.StartTime = DateTime.Parse(reader["mSTime"].ToString());
                next.EndTime = DateTime.Parse(reader["mETime"].ToString());
                next.Location.Id = int.Parse(reader["lId"].ToString());
                next.Location.Name = reader["lName"].ToString();
                next.Grounded = int.Parse(reader["mGrounded"].ToString());
                next.Type = selectAppointmentType(next);

                retAppointments.Add(next);
            }

            reader.Close();
            this.dbOra.Close();
            return retAppointments;
        }

        private EnumAppointmentType selectAppointmentType(Appointment appointment)
        {
            EnumAppointmentType retType = EnumAppointmentType.Appearance;

            OleDbCommand cmdSelect = dbOra.getMyOleDbConnection().CreateCommand();
            cmdSelect.CommandText = "select * from rehearsals where appointment_id = ?";
            cmdSelect.Parameters.AddWithValue("appId", appointment.Id);

            OleDbDataReader reader = cmdSelect.ExecuteReader();
            while (reader.Read())
            {
                retType = EnumAppointmentType.Rehearsal;
            }
            reader.Close();

            return retType;
        }

        private void printError(String message)
        {
            this.lblError.Content = message;
        }

        private void btnRemoveBandmember_Click(object sender, RoutedEventArgs e)
        {
            Musician selectedMusician = (Musician)this.dgCurrentBandmembers.SelectedItem;
            this.currentBand.Musicians.Remove(selectedMusician);
            this.dgCurrentBandmembers.Items.Refresh();

            this.dbOra.Connect();

            OleDbCommand cmdDeleteBandmember = dbOra.getMyOleDbConnection().CreateCommand();
            cmdDeleteBandmember.CommandText = "delete from bandmembers where band_id = ? and musician_id = ?";
            cmdDeleteBandmember.Parameters.AddWithValue("bandId", currentBand.Id);
            cmdDeleteBandmember.Parameters.AddWithValue("musicianId", selectedMusician.Id);
            cmdDeleteBandmember.ExecuteNonQuery();

            this.dbOra.Close();
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            this.dgCurrentBandmembers.Columns.ElementAt(2).Visibility = Visibility.Hidden;
            this.dgCurrentBandmembers.Columns.ElementAt(5).Visibility = Visibility.Hidden;
            this.dgCurrentBandmembers.Columns.ElementAt(6).Visibility = Visibility.Hidden;
            this.dgCurrentBandmembers.Columns.ElementAt(7).Visibility = Visibility.Hidden;

          //  this.dgAppointments.Columns.ElementAt(8).Visibility = Visibility.Hidden;

            this.dgCurrentBandmembers.Columns.ElementAt(0).Width = 50;
            this.dgCurrentBandmembers.Columns.ElementAt(1).Width = 160;
            this.dgCurrentBandmembers.Columns.ElementAt(3).Width = 160;
            this.dgCurrentBandmembers.Columns.ElementAt(4).Width = 160;

            this.tbBandCostsPerHour.Text = this.currentBand.CostsPerHour.ToString();

            this.cbLocations.ItemsSource = selectAllLocations();
            this.cbLocationsFixRehearsal.ItemsSource = this.cbLocations.ItemsSource;
        }

        private List<Model.Location> selectAllLocations()
        {
            List<Model.Location> retList = new List<Model.Location>();

            this.dbOra.Connect();

            OleDbCommand cmdSelectId = dbOra.getMyOleDbConnection().CreateCommand();
            cmdSelectId.CommandText = "select id, name from locations";

            OleDbDataReader reader = cmdSelectId.ExecuteReader(); //get password if musician already exists
            while (reader.Read())
            {
                retList.Add(new Model.Location(Int32.Parse(reader["id"].ToString()), reader["name"].ToString()));
            }

            reader.Close();
            this.dbOra.Close();

            return retList;
        }

        private void btnAcceptAppearanceRequest_Click(object sender, RoutedEventArgs e)
        { //remove from app requests and add to appointments and appearance
            AppearanceRequest requestToUpdate = (AppearanceRequest)this.dgReceivedAppearanceRequests.SelectedItem;
            
            this.dbOra.Connect();

            //remove from appRequests
            OleDbCommand cmdDeleteRequest = dbOra.getMyOleDbConnection().CreateCommand();
            cmdDeleteRequest.CommandText = "delete from appearance_requests where id = ?";
            cmdDeleteRequest.Parameters.AddWithValue("appReqId", requestToUpdate.Id);
            cmdDeleteRequest.ExecuteNonQuery();

            this.currentBand.AppearanceRequests.Remove(requestToUpdate);
            this.dgReceivedAppearanceRequests.Items.Refresh();

            //add to appointments
            Appointment newAppointment = new Appointment(requestToUpdate);

            OleDbCommand cmdinsertAppointment = dbOra.getMyOleDbConnection().CreateCommand();
            cmdinsertAppointment.CommandText = "insert into appointments values (0, ?, ?, ?, ?, ?, ?, ?)";
            cmdinsertAppointment.Parameters.AddWithValue("bandId", currentBand.Id);
            cmdinsertAppointment.Parameters.AddWithValue("locationId", newAppointment.Location.Id);
            cmdinsertAppointment.Parameters.AddWithValue("grounded", newAppointment.Grounded);
            cmdinsertAppointment.Parameters.AddWithValue("startTime", newAppointment.StartTime);
            cmdinsertAppointment.Parameters.AddWithValue("endTime", newAppointment.EndTime);
            cmdinsertAppointment.Parameters.AddWithValue("name", newAppointment.Name);
            cmdinsertAppointment.Parameters.AddWithValue("description", newAppointment.Description);
            cmdinsertAppointment.ExecuteNonQuery();

            //add to appearances and to bandList
            newAppointment.Id = selectAppointmentId(newAppointment);

            OleDbCommand cmdInsertAppearance = dbOra.getMyOleDbConnection().CreateCommand();
            cmdInsertAppearance.CommandText = "insert into appearances values (?, ?)";
            cmdInsertAppearance.Parameters.AddWithValue("appointmentId", newAppointment.Id);
            cmdInsertAppearance.Parameters.AddWithValue("bandId", currentBand.Id);
            cmdInsertAppearance.ExecuteNonQuery();

            this.currentBand.Appointments.Add(newAppointment);

            this.dbOra.Close();
            this.dgAppointments.Items.Refresh();
        }

        private int selectAppointmentId(Appointment newAppointment)
        {
            int retId = -1;

            OleDbCommand cmdSelectId = dbOra.getMyOleDbConnection().CreateCommand();
            cmdSelectId.CommandText = "select id  from appointments where band_id = ? and start_time = ? and end_time = ?";
            cmdSelectId.Parameters.AddWithValue("bandId", currentBand.Id);
            cmdSelectId.Parameters.AddWithValue("startTime", newAppointment.StartTime);
            cmdSelectId.Parameters.AddWithValue("endTime", newAppointment.EndTime);

            OleDbDataReader reader = cmdSelectId.ExecuteReader(); //get password if musician already exists
            while (reader.Read())
            {
                retId = Int32.Parse(reader["id"].ToString());
            }
            reader.Close();

            return retId;
        }

        private int selectRehearsalRequestId(RehearsalRequest rehRequest)
        {
            int retId = -1;

            OleDbCommand cmdSelectId = dbOra.getMyOleDbConnection().CreateCommand();
            cmdSelectId.CommandText = "select id  from rehearsal_requests where band_id = ? and start_time = ? and end_time = ?";
            cmdSelectId.Parameters.AddWithValue("bandId", currentBand.Id);
            cmdSelectId.Parameters.AddWithValue("startTime", rehRequest.StartTime);
            cmdSelectId.Parameters.AddWithValue("endTime", rehRequest.EndTime);

            OleDbDataReader reader = cmdSelectId.ExecuteReader(); //get password if musician already exists
            while (reader.Read())
            {
                retId = Int32.Parse(reader["id"].ToString());
            }
            reader.Close();

            return retId;
        }

        private void btnDeclineAppearanceRequest_Click(object sender, RoutedEventArgs e)
        {
            AppearanceRequest requestToUpdate = (AppearanceRequest)this.dgReceivedAppearanceRequests.SelectedItem;
            //remove from app requests

            this.dbOra.Connect();

            OleDbCommand cmdUpdateRequest = dbOra.getMyOleDbConnection().CreateCommand();
            cmdUpdateRequest.CommandText = "delete from appearance_requests where id = ?";
            cmdUpdateRequest.Parameters.AddWithValue("appReqId", requestToUpdate.Id);
            cmdUpdateRequest.ExecuteNonQuery();

            this.dbOra.Close();
            this.currentBand.AppearanceRequests.Remove(requestToUpdate);
            this.dgReceivedAppearanceRequests.Items.Refresh();
        }

        private void btnGroundAppointment_Click(object sender, RoutedEventArgs e)
        {
            Appointment appointmentToUpdate = (Appointment)this.dgAppointments.SelectedItem;

            if (appointmentToUpdate.HaveAllAccepted(currentBand))
            {
                //set grounded to one
                this.dbOra.Connect();

                OleDbCommand cmdUpdateRequest = dbOra.getMyOleDbConnection().CreateCommand();
                cmdUpdateRequest.CommandText = "update appointments set grounded = 1 where id = ?";
                cmdUpdateRequest.Parameters.AddWithValue("appReqId", appointmentToUpdate.Id);
                cmdUpdateRequest.ExecuteNonQuery();

                this.dbOra.Close();

                appointmentToUpdate.Grounded = 1;
                this.dgAppointments.Items.Refresh();
            }
            else
            {
                printError("Um Termin zu fixieren müssen diesen zuerst alle Mitglieder akzeptieren!");
            }
        }

        private void btnDeclineAppointment_Click(object sender, RoutedEventArgs e)
        {
            Appointment appointmentToUpdate = (Appointment)this.dgAppointments.SelectedItem;
            //remove from appointments and rehearsals / appearances
          
            this.dbOra.Connect();
            OleDbCommand cmdRemoveAppointment = dbOra.getMyOleDbConnection().CreateCommand();
            cmdRemoveAppointment.CommandText = "delete from " + appointmentToUpdate.Type + "s where appointment_id = ?";
            cmdRemoveAppointment.Parameters.AddWithValue("appId", appointmentToUpdate.Id);
            cmdRemoveAppointment.ExecuteNonQuery();

            cmdRemoveAppointment = dbOra.getMyOleDbConnection().CreateCommand();
            cmdRemoveAppointment.CommandText = "delete from appointments where id = ?";
            cmdRemoveAppointment.Parameters.AddWithValue("appId", appointmentToUpdate.Id);
            cmdRemoveAppointment.ExecuteNonQuery();

            this.dbOra.Close();
            this.currentBand.Appointments.Remove(appointmentToUpdate);
            this.dgAppointments.Items.Refresh();
        }

        private void btnCreateNewRehearsal_Click(object sender, RoutedEventArgs e)
        {
            RehearsalRequest rehRequest = new RehearsalRequest();

            try
            {
                rehRequest.StartTime = this.dtpNewReharsalStart.Value.Value;
                rehRequest.EndTime = this.dtpNewReharsalEnd.Value.Value;
                rehRequest.Duration = Double.Parse(this.tbNewRehearsalDuration.Text);

                if (rehRequest.StartTime != null && rehRequest.EndTime != null)
                {
                    //insert request and add to band
                    this.dbOra.Connect();

                    OleDbCommand cmdinsertRehRequest = dbOra.getMyOleDbConnection().CreateCommand();
                    cmdinsertRehRequest.CommandText = "insert into rehearsal_requests values (0, ?, ?, ?, ?)";
                    cmdinsertRehRequest.Parameters.AddWithValue("bandId", currentBand.Id);
                    cmdinsertRehRequest.Parameters.AddWithValue("startTime", rehRequest.StartTime);
                    cmdinsertRehRequest.Parameters.AddWithValue("endTime", rehRequest.EndTime);
                    cmdinsertRehRequest.Parameters.AddWithValue("duration", rehRequest.Duration);
                    cmdinsertRehRequest.ExecuteNonQuery();

                    rehRequest.Id = selectRehearsalRequestId(rehRequest);
                    this.currentBand.RehearsalRequests.Add(rehRequest);
                    this.dgRehearsalRequests.Items.Refresh();

                    this.dbOra.Close();
                }
            }
            catch (Exception)
            {
                this.printError("Felder nicht korrekt ausgefüllt!");
            }
        }

        private void btnRemoveRehearsalRequest_Click(object sender, RoutedEventArgs e)
        {
            RehearsalRequest requestToUpdate = (RehearsalRequest)this.dgRehearsalRequests.SelectedItem;
            //remove from app requests

            this.dbOra.Connect();

            OleDbCommand cmdUpdateRequest = dbOra.getMyOleDbConnection().CreateCommand();
            cmdUpdateRequest.CommandText = "delete from rehearsal_requests where id = ?";
            cmdUpdateRequest.Parameters.AddWithValue("rehReqId", requestToUpdate.Id);
            cmdUpdateRequest.ExecuteNonQuery();

            this.dbOra.Close();
            this.currentBand.RehearsalRequests.Remove(requestToUpdate);
            this.dgRehearsalRequests.Items.Refresh();
        }

        private void dgRehearsalRequests_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            RehearsalRequest rehRequestToFix = (RehearsalRequest)this.dgRehearsalRequests.SelectedItem;

            if (rehRequestToFix != null)
            {
                //select available times where person has min duration-time between start and end
                loadAllAvailableTimes();

                //calculate times where all members have time and add them to list
                List<AvailableTime> listEntries = calcBestTimesForRehRequest(rehRequestToFix);
                this.lvSuggestedTimesRehearsalRequest.ItemsSource = listEntries;
            }
            else
            {
                this.lvSuggestedTimesRehearsalRequest.ItemsSource = null;
            }
        }

        private void loadAllAvailableTimes()
        {
            this.dbOra.Connect();

            OleDbCommand cmdSelectId = dbOra.getMyOleDbConnection().CreateCommand();
            cmdSelectId.CommandText = "select id, musician_id, start_time, end_time from available_times where band_id = ?";
            cmdSelectId.Parameters.AddWithValue("bandId", currentBand.Id);

            foreach (Musician m in currentBand.MusiciansWithLeader)
            {
                m.AvailableTimes = new List<AvailableTime>();
            }

            OleDbDataReader reader = cmdSelectId.ExecuteReader(); //get password if musician already exists
            while (reader.Read())
            {
                Musician mus = this.currentBand.MusiciansWithLeader.FirstOrDefault(m => m.Id == Int32.Parse(reader["musician_id"].ToString()));
                mus.AvailableTimes.Add(new AvailableTime(Int32.Parse(reader["id"].ToString()), DateTime.Parse(reader["start_time"].ToString()), DateTime.Parse(reader["end_time"].ToString())));
            }

            reader.Close();
            this.dbOra.Close();
        }

        private List<AvailableTime> calcBestTimesForRehRequest(RehearsalRequest rehRequest)
        {
            List<AvailableTime> tempList = new List<AvailableTime>();
            List<AvailableTime> retList = new List<AvailableTime>();
         
            foreach (Musician m in currentBand.MusiciansWithLeader)
            {
                tempList.AddRange(from period in m.AvailableTimes
                                 where (period.StartTime >= rehRequest.StartTime && period.EndTime <= rehRequest.EndTime)
                                   || (period.EndTime >= rehRequest.StartTime && period.EndTime <= rehRequest.EndTime)
                                   || (period.StartTime <= rehRequest.StartTime && period.EndTime >= rehRequest.StartTime)
                                   || (period.StartTime <= rehRequest.StartTime && period.EndTime >= rehRequest.EndTime)
                                 select new AvailableTime
                                 {
                                     Id = m.Id,
                                     StartTime = new DateTime(Math.Max(period.StartTime.Ticks, rehRequest.StartTime.Ticks)),
                                     EndTime = new DateTime(Math.Min(period.EndTime.Ticks, rehRequest.EndTime.Ticks))
                                 });
            }

            //filter: min duration
            tempList = tempList.FindAll(at => (at.EndTime - at.StartTime).TotalHours >= rehRequest.Duration);

            //filter: only where all have time

            foreach (AvailableTime aT in tempList.FindAll(time => time.Id == currentBand.Leader.Id)) //loop through all fitting aTs of Leader
            {
                //calc if all other musicians have their time too, if yes: return dateTime
                AvailableTime calcedAvailableTime = calcOverlappingDateTimeOfAllMusicians(aT, tempList);

                if (calcedAvailableTime != null && ((calcedAvailableTime.EndTime - calcedAvailableTime.StartTime).TotalHours >= rehRequest.Duration))
                {
                    retList.Add(calcedAvailableTime);
                }
            }            

            return retList;
        }

        private AvailableTime calcOverlappingDateTimeOfAllMusicians(AvailableTime timeToFit, List<AvailableTime> possibleTimes)
        {
            Boolean hadOneFit;

            foreach (Musician m in currentBand.Musicians)
            {
                hadOneFit = false;

                foreach (AvailableTime aT in possibleTimes.FindAll(time => time.Id == m.Id))
                {
                    if (timeToFit.StartTime >= aT.StartTime && timeToFit.EndTime <= aT.EndTime
                                   || (timeToFit.EndTime >= aT.StartTime && timeToFit.EndTime <= aT.EndTime)
                                   || (timeToFit.StartTime <= aT.StartTime && timeToFit.EndTime >= aT.StartTime)
                                   || (timeToFit.StartTime <= aT.StartTime && timeToFit.EndTime >= aT.EndTime))
                    {
                        timeToFit.StartTime = new DateTime(Math.Max(timeToFit.StartTime.Ticks, aT.StartTime.Ticks));
                        timeToFit.EndTime = new DateTime(Math.Min(timeToFit.EndTime.Ticks, aT.EndTime.Ticks));

                        hadOneFit = true;
                        break;
                    }
                }

                if (hadOneFit == false)
                {
                    timeToFit = null;
                    break;
                }
            }

            return timeToFit;
        }

        private void btnUpdateBandData_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                int newCosts = int.Parse(this.tbBandCostsPerHour.Text);
                this.currentBand.CostsPerHour = newCosts;

                this.dbOra.Connect();

                OleDbCommand cmdUpdate = dbOra.getMyOleDbConnection().CreateCommand();
                cmdUpdate.CommandText = "update bands set costs_per_hour = ? where id = ?";
                cmdUpdate.Parameters.AddWithValue("costsPerHour",this.currentBand.CostsPerHour);
                cmdUpdate.Parameters.AddWithValue("bandId", this.currentBand.Id);

                cmdUpdate.ExecuteNonQuery();

                this.dbOra.Close();
            }
            catch (Exception)
            {
                this.printError("Daten nicht korrekt!");
            }
        }

        private void btnCreateAppointment_Click(object sender, RoutedEventArgs e)
        {
            String name = this.tbNameNewAppointment.Text;
            String description = this.tbDescriptionNewAppointment.Text;
            DateTime? startTime = this.dtpNewAppointmentStart.Value;
            DateTime? endTime = this.dtpNewAppointmentEnd.Value;
            Model.Location location = (Model.Location)this.cbLocations.SelectedItem;
            Appointment newAppointment = new Appointment(name, description, startTime, endTime, location, EnumAppointmentType.Appearance);

            this.dbOra.Connect();

            OleDbCommand cmdinsertAppointment = dbOra.getMyOleDbConnection().CreateCommand();
            cmdinsertAppointment.CommandText = "insert into appointments values (0, ?, ?, ?, ?, ?, ?, ?)";
            cmdinsertAppointment.Parameters.AddWithValue("bandId", currentBand.Id);
            cmdinsertAppointment.Parameters.AddWithValue("locationId", newAppointment.Location.Id);
            cmdinsertAppointment.Parameters.AddWithValue("grounded", newAppointment.Grounded);
            cmdinsertAppointment.Parameters.AddWithValue("startTime", newAppointment.StartTime);
            cmdinsertAppointment.Parameters.AddWithValue("endTime", newAppointment.EndTime);
            cmdinsertAppointment.Parameters.AddWithValue("name", newAppointment.Name);
            cmdinsertAppointment.Parameters.AddWithValue("description", newAppointment.Description);
            cmdinsertAppointment.ExecuteNonQuery();

            //add to appearances and to bandList
            newAppointment.Id = selectAppointmentId(newAppointment);

            OleDbCommand cmdInsertAppearance = dbOra.getMyOleDbConnection().CreateCommand();
            cmdInsertAppearance.CommandText = "insert into appearances values (?, ?)";
            cmdInsertAppearance.Parameters.AddWithValue("appointmentId", newAppointment.Id);
            cmdInsertAppearance.Parameters.AddWithValue("bandId", currentBand.Id);
            cmdInsertAppearance.ExecuteNonQuery();

            this.currentBand.Appointments.Add(newAppointment);

            this.dbOra.Close();
            this.dgAppointments.Items.Refresh();
        }

        private void btnAcceptRehearsalRequest_Click(object sender, RoutedEventArgs e) //TODO
        {
            RehearsalRequest rehReq = (RehearsalRequest)this.dgRehearsalRequests.SelectedItem;
            String name = this.tbNameFixRehearsal.Text;
            String description = this.tbDescriptionFixRehearsal.Text;
            AvailableTime mustBeBetweenTime = (AvailableTime)this.lvSuggestedTimesRehearsalRequest.SelectedItem;
            rehReq.StartTime = this.dtpFixRehearsalStart.Value.Value;
            rehReq.EndTime = rehReq.StartTime.AddHours(rehReq.Duration);
            Model.Location location = (Model.Location)this.cbLocationsFixRehearsal.SelectedItem;
            Appointment newAppointment = new Appointment(name, description, location, rehReq);
            newAppointment.Grounded = 1;
            //TODO: generate appointment_attendances!

            if (mustBeBetweenTime != null && newAppointment.StartTime >= mustBeBetweenTime.StartTime && newAppointment.EndTime <= mustBeBetweenTime.EndTime)
            {
                this.dbOra.Connect();

                OleDbCommand cmdinsertAppointment = dbOra.getMyOleDbConnection().CreateCommand();
                cmdinsertAppointment.CommandText = "insert into appointments values (0, ?, ?, ?, ?, ?, ?, ?)";
                cmdinsertAppointment.Parameters.AddWithValue("bandId", currentBand.Id);
                cmdinsertAppointment.Parameters.AddWithValue("locationId", newAppointment.Location.Id);
                cmdinsertAppointment.Parameters.AddWithValue("grounded", newAppointment.Grounded);
                cmdinsertAppointment.Parameters.AddWithValue("startTime", newAppointment.StartTime);
                cmdinsertAppointment.Parameters.AddWithValue("endTime", newAppointment.EndTime);
                cmdinsertAppointment.Parameters.AddWithValue("name", newAppointment.Name);
                cmdinsertAppointment.Parameters.AddWithValue("description", newAppointment.Description);
                cmdinsertAppointment.ExecuteNonQuery();

                //add to appearances and to bandList
                newAppointment.Id = selectAppointmentId(newAppointment);

                OleDbCommand cmdInsertAppearance = dbOra.getMyOleDbConnection().CreateCommand();
                cmdInsertAppearance.CommandText = "insert into rehearsals values (?, ?)";
                cmdInsertAppearance.Parameters.AddWithValue("appointmentId", newAppointment.Id);
                cmdInsertAppearance.Parameters.AddWithValue("bandId", currentBand.Id);
                cmdInsertAppearance.ExecuteNonQuery();

                this.currentBand.Appointments.Add(newAppointment);

                //remove 
                //OleDbCommand cmdDeleteRequest = dbOra.getMyOleDbConnection().CreateCommand();
                //cmdDeleteRequest.CommandText = "delete from rehearsal_requests where id = ?";
                //cmdDeleteRequest.Parameters.AddWithValue("rehReqId", rehReq.Id);
                //cmdDeleteRequest.ExecuteNonQuery();

               // this.currentBand.RehearsalRequests.Remove(rehReq);

                this.dbOra.Close();
                this.dgAppointments.Items.Refresh();
                this.dgRehearsalRequests.Items.Refresh();
            }
            else
            {
                this.printError("Mögliche Zeit muss selektiert werden und eingetragene Startzeit(+ Probedauer) muss in diesem Intervall liegen!");
            }
        }

        private void dgAppointments_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            Appointment selAppointment = (Appointment)this.dgAppointments.SelectedItem;

            if (selAppointment != null)
            {
                selAppointment.MusicianAnswers = loadMusicianAnswersFromAppointment(selAppointment);
                this.lvMusicianAnswers.ItemsSource = selAppointment.MusicianAnswers;
            }
            else
            {
                this.lvMusicianAnswers.ItemsSource = null;
            }
        }

        private Dictionary<String, int> loadMusicianAnswersFromAppointment(Appointment selAppointment)
        {
            Dictionary<String, int> retDict = new Dictionary<String, int>();

            this.dbOra.Connect();

            OleDbCommand cmdSelectMusicianData = dbOra.getMyOleDbConnection().CreateCommand();
            cmdSelectMusicianData.CommandText = @"select m.username as mUsername, a.accepted as aAccepted from appointment_attendances a join musicians m on a.musician_id = m.id 
                                                  where a.band_id = ? and a.appointment_id = ?";
            cmdSelectMusicianData.Parameters.AddWithValue("bandId", currentBand.Id);
            cmdSelectMusicianData.Parameters.AddWithValue("appId", selAppointment.Id);

            OleDbDataReader reader = cmdSelectMusicianData.ExecuteReader(); //get password if musician already exists
            while (reader.Read())
            {
                retDict.Add(reader["mUsername"].ToString(), int.Parse(reader["aAccepted"].ToString()));
            }

            reader.Close();
            this.dbOra.Close();

            return retDict;
        }
    }
}
