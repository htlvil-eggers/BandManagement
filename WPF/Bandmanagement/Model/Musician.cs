using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bandmanagement.Model
{
    public class Musician
    {
        [JsonProperty(PropertyName = "id")]
        public int Id { get; set; }

        [JsonProperty(PropertyName = "username")]
        public String Username { get; set; }

        [JsonProperty(PropertyName = "password")]
        public String Password { get; set; }

        [JsonProperty(PropertyName = "firstName")]
        public String FirstName { get; set; }

        [JsonProperty(PropertyName = "lastName")]
        public String LastName { get; set; }

        [JsonProperty(PropertyName = "birthdate")]
        public DateTime? Birthdate { get; set; }

        [JsonProperty(PropertyName = "habitation")]
        public Location Habitation { get; set; }

        [JsonProperty(PropertyName = "availableTimes")]
        public List<AvailableTime> AvailableTimes { get; set; }

        [JsonProperty(PropertyName = "skills")]
        public List<Instrument> Skills { get; set; } 


        public Musician()
        {
            this.AvailableTimes = new List<AvailableTime>();
        }

        public Musician (int id, String username, String password, String firstName, String lastName)
        {
            this.Id = id;
            this.Username = username;
            this.Password = password;
            this.FirstName = firstName;
            this.LastName = lastName;

            this.Birthdate = null;
            this.Habitation = new Location();
            this.Skills = null;
            this.AvailableTimes = new List<AvailableTime>();
        }

        public Boolean IsDataCorrect()
        {
            Boolean dataCorrect = true;

            if (this.Username == null || this.Password == null || this.FirstName == null || this.LastName == null ||
                this.Username.Length < 1 || this.Password.Length < 1 || this.FirstName.Length < 1 || this.LastName.Length < 1)
            {
                dataCorrect = false;
            }

            return dataCorrect;
        }
    }
}
