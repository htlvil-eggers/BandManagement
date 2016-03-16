using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Http;
using System.Net;
using System.IO;
using Newtonsoft.Json;
using System.Collections;

namespace Bandmanagement.Model
{
    public class WebserviceManager
    {
        private static HttpClient client
        {
            get; set;
        }

        private static readonly String Uri = "http://localhost:8081/BandManagement_Webserver";

        public static Boolean AddMusician(Musician m)
        {
            WebResponse resp = POST(Uri + "/rest/musicians", JsonConvert.SerializeObject(m));

            if (((HttpWebResponse)resp).StatusCode != HttpStatusCode.OK || ((HttpWebResponse)resp).StatusCode != HttpStatusCode.Created ||
                ((HttpWebResponse)resp).StatusCode != HttpStatusCode.NoContent)
            {
                return false;
            }

            return true;
        }

        public static Boolean AddBand(Band b, String username)
        {          
            AddBandWrapper abWrapper = new AddBandWrapper(b, username);
            WebResponse resp = POST(Uri + "/rest/bands", JsonConvert.SerializeObject(abWrapper));            

            if (((HttpWebResponse)resp).StatusCode != HttpStatusCode.OK || ((HttpWebResponse)resp).StatusCode != HttpStatusCode.Created)
            {
                return false;
            }

            return true;
        }

        public static List<AppearanceRequest> GetAppearanceRequestsOfBand(string name)
        {
            String data = GET(Uri + "/rest/bands/appearanceRequests/" + name);

            return ((AppearanceRequest[])JsonConvert.DeserializeObject(data, typeof(AppearanceRequest[]))).ToList<AppearanceRequest>();
        }

        public static List<Appointment> GetAppointmentsOfBand(string name)
        {
            String data = GET(Uri + "/rest/bands/appointments/" + name);

            return ((Appointment[])JsonConvert.DeserializeObject(data, typeof(Appointment[]))).ToList<Appointment>();
        }

        public static List<RehearsalRequest> GetRehearsalRequestsOfBand(string name)
        {
            String data = GET(Uri + "/rest/rehearsalRequests/" + name);

            return ((RehearsalRequest[])JsonConvert.DeserializeObject(data, typeof(RehearsalRequest[]))).ToList<RehearsalRequest>();
        }

        public static List<Musician> GetMusiciansOfBand(String bandname)
        {
            String data = GET(Uri + "/rest/bands/musicians/" + bandname);

            return ((Musician[])JsonConvert.DeserializeObject(data, typeof(Musician[]))).ToList<Musician>();
        }

        public static int GetIdFromBandname(String name) //TODO
        {
            String data = GET(Uri + "/rest/bands/" + name);

            return int.Parse(JsonConvert.DeserializeObject<String>(data));
        }

        public static Musician GetMusician(string username, string password)
        {
            String data = GET(Uri + "/rest/musicians/" + username + "/" + password);

            return (Musician) JsonConvert.DeserializeObject(data, typeof(Musician));
        }

        public static Boolean AddMusicianToBand(Band b, string username)
        {
            AddBandWrapper abWrapper = new AddBandWrapper(b, username);
            WebResponse resp = POST(Uri + "/rest/bands/members", JsonConvert.SerializeObject(abWrapper));

            if (((HttpWebResponse)resp).StatusCode != HttpStatusCode.OK || ((HttpWebResponse)resp).StatusCode != HttpStatusCode.Created)
            {
                return false;
            }

            return true;
        }

        public static int AddLocation(Location locToInsert, double x, double y)
        {
            WebResponse resp = POST(Uri + "/rest/locations", JsonConvert.SerializeObject(new AddSpatialWrapper(locToInsert, x, y)));

            String id = new StreamReader(resp.GetResponseStream()).ReadToEnd();
            
            return int.Parse(JsonConvert.DeserializeObject<String>(id));
        }

        public static Band GetBandOfLeader(string name, string username)
        {
            String data = GET(Uri + "/rest/bands/" + name + "/" + username);

            return (Band)JsonConvert.DeserializeObject(data, typeof(Band));
        }

        public static List<Location> GetLocations()
        {
            String data = GET(Uri + "/rest/locations");

            return ((Location[])JsonConvert.DeserializeObject(data, typeof(Location[]))).ToList<Location>();
        }

        public static void RemoveMusicianFromBand(Band b, string username)
        {
            AddBandWrapper abWrapper = new AddBandWrapper(b, username);
            DELETE(Uri + "/rest/bands/members", JsonConvert.SerializeObject(abWrapper));
        }

        private static String GET(String uri)
        {
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(uri);
            request.Method = WebRequestMethods.Http.Get;
            request.Accept = "application/json";  

            return new StreamReader(request.GetResponse().GetResponseStream()).ReadToEnd();
        }

        public static void UpdateLocation(Location loc, double x, double y)
        {
            PUT(Uri + "/rest/locations", JsonConvert.SerializeObject(new AddSpatialWrapper(loc, x, y)));
        }

        public static void UpdateStreet(Street street, double x1, double y1, double x2, double y2)
        {
            PUT(Uri + "/rest/streets", JsonConvert.SerializeObject(new AddSpatialWrapper(street, x1, x2, y1, y2)));
        }

        private static WebResponse POST (String uri, String data)
        {
            WebRequest request = WebRequest.Create(uri);
            request.Method = "POST";
            request.ContentType = "application/json";
            StreamWriter dataStream = new StreamWriter(request.GetRequestStream());
            dataStream.Write(data);
            dataStream.Flush();
            dataStream.Close();

            return request.GetResponse();
        }

        private static WebResponse PUT(String uri, String data)
        {
            WebRequest request = WebRequest.Create(uri);
            request.Method = "PUT";
            request.ContentType = "application/json";
            StreamWriter dataStream = new StreamWriter(request.GetRequestStream());
            dataStream.Write(data);
            dataStream.Flush();
            dataStream.Close();

            return request.GetResponse();
        }

        public static int AddStreet(Street street, double x1, double y1, double x2, double y2)
        {
            WebResponse resp = POST(Uri + "/rest/streets", JsonConvert.SerializeObject(new AddSpatialWrapper(street, x1, x2, y1, y2)));

            String id = new StreamReader(resp.GetResponseStream()).ReadToEnd();
            return int.Parse(JsonConvert.DeserializeObject<String>(id));
        }

        private static WebResponse DELETE(String uri, String data)
        {
            WebRequest request = WebRequest.Create(uri);
            request.Method = "DELETE";
            request.ContentType = "application/json";
            StreamWriter dataStream = new StreamWriter(request.GetRequestStream());
            dataStream.Write(data);
            dataStream.Flush();
            dataStream.Close();

            return request.GetResponse();
        }

        public static void RemoveAppearanceRequest(int id)
        {
            DELETE(Uri + "/rest/appearanceRequests", JsonConvert.SerializeObject(id.ToString()));
        }

        public static int AddAppointment(Appointment newAppointment, Band b)
        {
            AddBandWrapper abWrapper = new AddBandWrapper(b, newAppointment);
            WebResponse resp = POST(Uri + "/rest/bands/appointments", JsonConvert.SerializeObject(abWrapper));

            String id = new StreamReader(resp.GetResponseStream()).ReadToEnd();
            return int.Parse(JsonConvert.DeserializeObject<String>(id));
        }

        public static int AddAppearance(Band b, Appointment newAppointment)
        {
            AddBandWrapper abWrapper = new AddBandWrapper(b, newAppointment);
            WebResponse resp = POST(Uri + "/rest/bands/appearances", JsonConvert.SerializeObject(abWrapper));

            String id = new StreamReader(resp.GetResponseStream()).ReadToEnd();
            return int.Parse(JsonConvert.DeserializeObject<String>(id));
        }

        public static void GroundAppointment(int id)
        {
            POST(Uri + "/rest/appointments", JsonConvert.SerializeObject(id.ToString()));
        }

        public static bool AllMembersOfBandAccepted(Band currentBand, Appointment appointmentToUpdate)
        {
            String data = GET(Uri + "/rest/musicians/"+ currentBand.Name + "/" + appointmentToUpdate.Id);

            return Boolean.Parse(JsonConvert.DeserializeObject<String>(data));
        }

        public static void RemoveAppointment(Appointment app)
        {
            DELETE(Uri + "/rest/appointments", JsonConvert.SerializeObject(app));
        }

        public static int AddRehearsalRequest(Band b, RehearsalRequest rehRequest)
        {
            AddBandWrapper abWrapper = new AddBandWrapper(b, rehRequest);
            WebResponse resp = POST(Uri + "/rest/bands/rehearsalRequests", JsonConvert.SerializeObject(abWrapper));

            String id = new StreamReader(resp.GetResponseStream()).ReadToEnd();
            return int.Parse(JsonConvert.DeserializeObject<String>(id));
        }

        public static void RemoveRehearsalRequest(int id)
        {
            DELETE(Uri + "/rest/bands/rehearsalRequests", JsonConvert.SerializeObject(id.ToString()));
        }

        public static Band GetAvailableTimesOfBand(Band currentBand)
        {
            WebResponse resp = PUT(Uri + "/rest/bands/availableTimes", JsonConvert.SerializeObject(currentBand));

            String data = new StreamReader(resp.GetResponseStream()).ReadToEnd();
            return (Band)JsonConvert.DeserializeObject<Band>(data);
        }

        public static void UpdateBand(Band currentBand)
        {
            PUT(Uri + "/rest/bands", JsonConvert.SerializeObject(currentBand));
        }

        public static Dictionary<String, int> AllAnswersOfAppointment(String bandname, int appId)
        {
            String data = GET(Uri + "/rest/bands/answers" + bandname + "/" + appId);

            return (Dictionary<String, int>)JsonConvert.DeserializeObject<Dictionary<String, int>>(data);
        }

        public static List<AddSpatialWrapper> GetLocationsSpatial()
        {
            String data = GET(Uri + "/rest/locations/spatial");

            return ((AddSpatialWrapper[])JsonConvert.DeserializeObject(data, typeof(AddSpatialWrapper[]))).ToList<AddSpatialWrapper>();
        }

        public static List<AddSpatialWrapper> GetStreetsSpatial()
        {
            String data = GET(Uri + "/rest/streets/spatial");

            return ((AddSpatialWrapper[])JsonConvert.DeserializeObject(data, typeof(AddSpatialWrapper[]))).ToList<AddSpatialWrapper>();
        }

        public static void DeleteLocation(int id)
        {
            DELETE(Uri + "/rest/locations", JsonConvert.SerializeObject(id.ToString()));
        }

        public static void DeleteStreet(int id)
        {
            DELETE(Uri + "/rest/streets", JsonConvert.SerializeObject(id.ToString()));
        }
    }
}
