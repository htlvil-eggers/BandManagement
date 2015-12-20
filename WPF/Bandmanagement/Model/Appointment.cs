using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bandmanagement.Model
{
    public class Appointment
    {
        public int Id { get; set; }

        public String Name { get; set; }

        public String Description { get; set; }

        public DateTime? StartTime { get; set; }

        public DateTime? EndTime { get; set; }

        public Location Location { get; set; }

        public int Grounded { get; set; }

        public EnumAppointmentType Type { get; set; }

        public Dictionary<String, int> MusicianAnswers { get; set; }


        public Appointment ()
        {
            this.Grounded = 0;
            this.Location = new Location();
            this.MusicianAnswers = new Dictionary<string, int>();
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
            this.Type = EnumAppointmentType.Appearance;
            this.MusicianAnswers = new Dictionary<string, int>();
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
            this.MusicianAnswers = new Dictionary<string, int>();
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
            this.MusicianAnswers = new Dictionary<string, int>();
        }

        public bool HaveAllAccepted(Band b)
        {
            return this.MusicianAnswers.Values.Select(ma => ma == 1).Count() > b.MusiciansWithLeader.Count;
        }
    }
}
