using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bandmanagement.Model
{
    public class Band
    {
        [JsonProperty(PropertyName = "id")]
        public int Id { get; set; }

        [JsonProperty(PropertyName = "name")]
        public String Name { get; set; }

        [JsonProperty(PropertyName = "leader")]
        public Musician Leader { get; set; }

        [JsonProperty(PropertyName = "costsPerHour")]
        public int CostsPerHour { get; set; }

        [JsonProperty(PropertyName = "musicians")]
        public List<Musician> Musicians { get; set; }

        [JsonProperty(PropertyName = "appearanceRequests")]
        public List<AppearanceRequest> AppearanceRequests { get; set; }

        [JsonProperty(PropertyName = "rehearsalRequests")]
        public List<RehearsalRequest> RehearsalRequests { get; set; }

        [JsonProperty(PropertyName = "appointments")]
        public List<Appointment> Appointments { get; set; }

        [JsonProperty(PropertyName = "musiciansWithLeader")]
        public List<Musician> MusiciansWithLeader { get
            {
                List<Musician> retList = new List<Musician>();
                retList.AddRange(this.Musicians);
                retList.Add(this.Leader);

                return retList;
            }
        }


        public Band()
        {
            this.Musicians = new List<Musician>();
            this.AppearanceRequests = new List<AppearanceRequest>();
            this.Appointments = new List<Appointment>();
            this.RehearsalRequests = new List<RehearsalRequest>();
        }
         
        public Band (int id, String name, Musician leader)
        {
            this.Id = id;
            this.Name = name;
            this.Leader = leader;
            this.Musicians = new List<Musician>();
            this.AppearanceRequests = new List<AppearanceRequest>();
            this.Appointments = new List<Appointment>();
            this.RehearsalRequests = new List<RehearsalRequest>();
        }

        public Boolean IsDataCorrect()
        {
            Boolean dataCorrect = true;

            if (this.Name.Length < 1)
            {
                dataCorrect = false;
            }

            return dataCorrect;
        }
    }
}
