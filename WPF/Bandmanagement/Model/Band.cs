using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bandmanagement.Model
{
    public class Band
    {
        public int Id { get; set; }

        public String Name { get; set; }

        public Musician Leader { get; set; }

        public int CostsPerHour { get; set; }



        public Band()
        {

        }

        public Band (int id, String name, Musician leader)
        {
            this.Id = id;
            this.Name = name;
            this.Leader = leader;
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
