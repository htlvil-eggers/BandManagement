using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bandmanagement.Model
{
    public class AppearanceRequest
    {
        public int Id { get; set; }

        public String Name { get; set; }

        public String Description { get; set; }

        public DateTime StartTime { get; set; }

        public DateTime EndTime { get; set; }

        public Location Location { get; set; }

        public int Accepted { get; set; }

        
        public AppearanceRequest()
        {
            this.Location = new Location();
            this.Accepted = 0;
        }
    }
}
