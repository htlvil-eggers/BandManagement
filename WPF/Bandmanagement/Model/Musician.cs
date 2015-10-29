using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bandmanagement.Model
{
    public class Musician
    {
        public int Id { get; set; }

        public String Username { get; set; }

        public String Password { get; set; }

        public String FirstName { get; set; }

        public String LastName { get; set; }

        public DateTime? Birthdate { get; set; }

        public Location Habitation { get; set; }

        public List<Instrument> Skills { get; set; }  //TODO: instead of table instrumentSkills; correct collection?


        public Musician()
        {
            
        }

        public Musician (int id, String username, String password, String firstName, String lastName)
        {
            this.Id = id;
            this.Username = username;
            this.Password = password;
            this.FirstName = firstName;
            this.LastName = lastName;

            this.Birthdate = null;
            this.Habitation = null;
            this.Skills = null;
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
