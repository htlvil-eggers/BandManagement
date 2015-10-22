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

        public DateTime Birtdate { get; set; }

        //public int HabitationId { get; set; }
    }
}
