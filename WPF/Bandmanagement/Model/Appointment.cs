using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bandmanagement.Model
{
    public class Appointment
    {
        [JsonProperty(PropertyName = "id")]
        public int Id { get; set; }

        [JsonProperty(PropertyName = "name")]
        public String Name { get; set; }

        [JsonProperty(PropertyName = "description")]
        public String Description { get; set; }

        [JsonProperty(PropertyName = "startTime")]
        public DateTime? StartTime { get; set; }

        [JsonProperty(PropertyName = "endTime")]
        public DateTime? EndTime { get; set; }

        [JsonProperty(PropertyName = "location")]
        public Location Location { get; set; }

        [JsonProperty(PropertyName = "grounded")]
        public int Grounded { get; set; }

        [JsonProperty(PropertyName = "type")]
        public EnumAppointmentType Type { get; set; }

       /* [JsonProperty(PropertyName = "musicianAnswers")]
        public Dictionary<String, int> MusicianAnswers { get; set; }  //hashMap there?*/


        public Appointment ()
        {
            this.Grounded = 0;
            this.Location = new Location();
        }

        public Appointment (AppearanceRequest appRequest)
        {
            this.Id = appRequest.Id;
            this.Name = appRequest.Name;
            this.Description = appRequest.Description;
            this.StartTime = appRequest.StartTime;
            this.EndTime = appRequest.EndTime;
            this.Location = appRequest.Location;
            this.Grounded = 0;
            this.Type = EnumAppointmentType.Appearance;;
        }

        public Appointment(String name, String description, DateTime? startTime, DateTime? endTime, Location location, EnumAppointmentType type)
        {
            this.Name = name;
            this.Description = description;
            this.StartTime = startTime;
            this.EndTime = endTime;
            this.Location = location;
            this.Grounded = 0;
            this.Type = type;
        }

        public Appointment(string name, string description, Location location, RehearsalRequest rehReq)
        {
            this.Id = rehReq.Id;
            this.Name = name;
            this.Description = description;
            this.StartTime = rehReq.StartTime;
            this.EndTime = rehReq.EndTime;
            this.Location = location;
            this.Grounded = 0;
            this.Type = EnumAppointmentType.Rehearsal;
        }
    }
}
