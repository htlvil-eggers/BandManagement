using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bandmanagement.Model
{
    public class BandMembership
    {
        public Band Band { get; set; }

        public Musician Musician { get; set; }

        public DateTime Joined { get; set; }
    }
}
