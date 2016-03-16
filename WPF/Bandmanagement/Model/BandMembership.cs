using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bandmanagement.Model
{
    public class BandMembership
    {
        [JsonProperty(PropertyName = "band")]
        public Band Band { get; set; }

        [JsonProperty(PropertyName = "musician")]
        public Musician Musician { get; set; }

        [JsonProperty(PropertyName = "joined")]
        public DateTime Joined { get; set; }
    }
}
