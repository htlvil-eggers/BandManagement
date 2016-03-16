using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bandmanagement.Model
{
    public class Location
    {
        [JsonProperty(PropertyName = "id")]
        public int Id { get; set; }

        [JsonProperty(PropertyName = "name")]
        public String Name { get; set; }

        public Location()
        {
        }

        public Location (int _Id, String _Name)
        {
            Id = _Id;
            Name = _Name;
        }

        public override string ToString()
        {
            return Name;
        }
    }
}
