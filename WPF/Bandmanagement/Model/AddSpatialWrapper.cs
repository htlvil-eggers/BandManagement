using Bandmanagement.Model;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bandmanagement.Model
{
    public class AddSpatialWrapper
    {
        [JsonProperty(PropertyName = "location")]
        public Location Location { get; set; }

        [JsonProperty(PropertyName = "street")]
        public Street Street { get; set; }

        [JsonProperty(PropertyName = "x")]
        public double X { get; set; }

        [JsonProperty(PropertyName = "x2")]
        public double X2 { get; set; }

        [JsonProperty(PropertyName = "y")]
        public double Y { get; set; }

        [JsonProperty(PropertyName = "y2")]
        public double Y2 { get; set; }

        public AddSpatialWrapper()
        {

        }

        public AddSpatialWrapper(Location l, double x, double y)
        {
            Location = l;
            X = x;
            Y = y;
        }

        public AddSpatialWrapper(Street s, double x1, double x2, double y1, double y2)
        {
            Street = s;
            X = x1;
            X2 = x2;

            Y = y1;
            Y2 = y2;
        }
    }
}
