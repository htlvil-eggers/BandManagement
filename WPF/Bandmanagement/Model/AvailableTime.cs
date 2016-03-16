using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bandmanagement.Model
{
    public class AvailableTime
    {
        [JsonProperty(PropertyName = "id")]
        public int Id { get; set; }

        [JsonProperty(PropertyName = "startTime")]
        public DateTime StartTime { get; set; }

        [JsonProperty(PropertyName = "endTime")]
        public DateTime EndTime { get; set; }

        public AvailableTime()
        {

        }

        public AvailableTime(int id, DateTime startTime, DateTime endTime)
        {
            Id = id;
            StartTime = startTime;
            EndTime = endTime;
        }

        public override string ToString()
        {
            return "StartTime: " + StartTime + ", EndTime: " + EndTime;
        }
    }
}
