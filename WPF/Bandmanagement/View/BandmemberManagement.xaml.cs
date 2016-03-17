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
        public Band currentBand;

        public BandmemberManagement(Band signedInBand)
        {
            InitializeComponent();

            currentBand = signedInBand;

            //set datagrids
            this.currentBand.Musicians = WebserviceManager.GetMusiciansOfBand(currentBand.Name); 
            this.dgCurrentBandmembers.ItemsSource = this.currentBand.Musicians;

            this.currentBand.AppearanceRequests = WebserviceManager.GetAppearanceRequestsOfBand(currentBand.Name);  
            this.dgReceivedAppearanceRequests.ItemsSource = this.currentBand.AppearanceRequests;

            this.currentBand.Appointments = WebserviceManager.GetAppointmentsOfBand(currentBand.Name);  
            this.dgAppointments.ItemsSource = this.currentBand.Appointments;

            this.currentBand.RehearsalRequests = WebserviceManager.GetRehearsalRequestsOfBand(currentBand.Name);
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
                if (WebserviceManager.AddMusician(insertedMusician))
                {
                    //insert bandmember
                    if (WebserviceManager.AddMusicianToBand(currentBand, insertedMusician.Username) == false)
                    {
                        this.printError("Musiker bereits in eigener Band!");
                    }
                }
                else if (Xceed.Wpf.Toolkit.MessageBox.Show("User existiert bereits", 
                        "Möchten sie den User trotzdem zur Band hinzufügen?", MessageBoxButton.YesNo, MessageBoxImage.Question) == MessageBoxResult.Yes)
                {
                    WebserviceManager.AddMusicianToBand(currentBand, insertedMusician.Username);
                }
            }
            else
            {
                this.printError("Alle Felder müssen ausgefüllt werden!");
            }

            this.currentBand.Musicians = WebserviceManager.GetMusiciansOfBand(currentBand.Name);
            this.dgCurrentBandmembers.ItemsSource = this.currentBand.Musicians;
            this.Window_Loaded(this, null);
        }

        private void printError(String message)
        {
            this.lblError.Content = message;
        }

        private void btnRemoveBandmember_Click(object sender, RoutedEventArgs e)
        {
            Musician selectedMusician = (Musician)this.dgCurrentBandmembers.SelectedItem;
            WebserviceManager.RemoveMusicianFromBand(currentBand, selectedMusician.Username);

            this.currentBand.Musicians.Remove(selectedMusician);
            this.dgCurrentBandmembers.ItemsSource = this.currentBand.Musicians;
            this.Window_Loaded(this, null);
            this.dgCurrentBandmembers.Items.Refresh();
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

            if (e != null)
            {
                this.cbLocations.ItemsSource = WebserviceManager.GetLocations();
                this.cbLocationsFixRehearsal.ItemsSource = this.cbLocations.ItemsSource;
            }
        }

        private void btnAcceptAppearanceRequest_Click(object sender, RoutedEventArgs e)
        { //remove from app requests and add to appointments and appearance
            AppearanceRequest requestToUpdate = (AppearanceRequest)this.dgReceivedAppearanceRequests.SelectedItem;

            //remove from appRequests
            WebserviceManager.RemoveAppearanceRequest(requestToUpdate.Id);

            this.currentBand.AppearanceRequests.Remove(requestToUpdate);
            this.dgReceivedAppearanceRequests.Items.Refresh();

            //add to appointments
            Appointment newAppointment = new Appointment(requestToUpdate);

            //TODO-----------------------
            newAppointment.Id = WebserviceManager.AddAppointment(newAppointment, currentBand);

            WebserviceManager.AddAppearance(currentBand, newAppointment);
            this.currentBand.Appointments.Add(newAppointment);

            this.dgAppointments.Items.Refresh();
        }

        private void btnDeclineAppearanceRequest_Click(object sender, RoutedEventArgs e) 
        {
            AppearanceRequest requestToUpdate = (AppearanceRequest)this.dgReceivedAppearanceRequests.SelectedItem;
            //remove from app requests
            WebserviceManager.RemoveAppearanceRequest(requestToUpdate.Id);

            this.currentBand.AppearanceRequests.Remove(requestToUpdate);
            this.dgReceivedAppearanceRequests.Items.Refresh();
        }

        private void btnGroundAppointment_Click(object sender, RoutedEventArgs e) 
        {
            Appointment appointmentToUpdate = (Appointment)this.dgAppointments.SelectedItem;

            if (WebserviceManager.AllMembersOfBandAccepted(currentBand, appointmentToUpdate))
            {
                //set grounded to one
                WebserviceManager.GroundAppointment(appointmentToUpdate.Id);

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

            WebserviceManager.RemoveAppointment(appointmentToUpdate);

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
                    rehRequest.Id = WebserviceManager.AddRehearsalRequest(currentBand, rehRequest);

                    this.currentBand.RehearsalRequests.Add(rehRequest);
                    this.dgRehearsalRequests.Items.Refresh();
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
            WebserviceManager.RemoveRehearsalRequest(requestToUpdate.Id);
            this.currentBand.RehearsalRequests.Remove(currentBand.RehearsalRequests.FirstOrDefault(r => r.Id == requestToUpdate.Id));
            this.dgRehearsalRequests.ItemsSource = this.currentBand.RehearsalRequests;
            this.Window_Loaded(this, null);
        }

        private void dgRehearsalRequests_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            RehearsalRequest rehRequestToFix = (RehearsalRequest)this.dgRehearsalRequests.SelectedItem;

            if (rehRequestToFix != null)
            {
                //select available times where person has min duration-time between start and end
                currentBand = WebserviceManager.GetAvailableTimesOfBand(currentBand);  //set the ones to the mitarbeiter and send band back?

                //calculate times where all members have time and add them to list
                List<AvailableTime> listEntries = calcBestTimesForRehRequest(rehRequestToFix);
                this.lvSuggestedTimesRehearsalRequest.ItemsSource = listEntries;
            }
            else
            {
                this.lvSuggestedTimesRehearsalRequest.ItemsSource = null;
            }
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
            currentBand.CostsPerHour = int.Parse(this.tbBandCostsPerHour.Text);
            WebserviceManager.UpdateBand(currentBand);
        }

        private void btnCreateAppointment_Click(object sender, RoutedEventArgs e)
        {
            String name = this.tbNameNewAppointment.Text;
            String description = this.tbDescriptionNewAppointment.Text;
            DateTime? startTime = this.dtpNewAppointmentStart.Value;
            DateTime? endTime = this.dtpNewAppointmentEnd.Value;
            Model.Location location = (Model.Location)this.cbLocations.SelectedItem;
            Appointment newAppointment = new Appointment(name, description, startTime, endTime, location, EnumAppointmentType.Appearance);

            newAppointment.Id = WebserviceManager.AddAppointment(newAppointment, currentBand);               
            WebserviceManager.AddAppearance(currentBand, newAppointment);           

            this.currentBand.Appointments.Add(newAppointment);
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
                newAppointment.Id = WebserviceManager.AddAppointment(newAppointment, currentBand);
                WebserviceManager.AddAppearance(currentBand, newAppointment);

                this.currentBand.Appointments.Add(newAppointment);
                this.dgAppointments.ItemsSource = currentBand.Appointments;
                this.Window_Loaded(this, null);
                this.dgRehearsalRequests.Items.Refresh();
            }
            else
            {
                this.printError("Mögliche Zeit muss selektiert werden und eingetragene Startzeit(+ Probedauer) muss in diesem Intervall liegen!");
            }
        }

        private void dgAppointments_SelectionChanged(object sender, SelectionChangedEventArgs e)  //TODO
        {
            Appointment selAppointment = (Appointment)this.dgAppointments.SelectedItem;

            if (selAppointment != null)
            {
                this.lvMusicianAnswers.ItemsSource = WebserviceManager.AllAnswersOfAppointment(currentBand.Name, selAppointment.Id);
            }
            else
            {
                this.lvMusicianAnswers.ItemsSource = null;
            }
        }
    }
}
