using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bandmanagement.Model
{
    public class AddBandWrapper
    {
        [JsonProperty(PropertyName = "band")]
        private Band Band { get; set; }

        [JsonProperty(PropertyName = "username")]
        private String Username { get; set; }

        [JsonProperty(PropertyName = "appointment")]
        private Appointment Appointment { get; set; }

        [JsonProperty(PropertyName = "rehearsalRequest")]
        private RehearsalRequest RehearsalRequest { get; set; }

        [JsonProperty(PropertyName = "appearanceRequest")]
        private AppearanceRequest AppearanceRequest { get; set; }

        public AddBandWrapper(Band b, string username)
        {
            Band = b;
            Username = username;
        }

        public AddBandWrapper(Band b, Appointment appointment)
        {
            Band = b;
            Appointment = appointment;
        }

        public AddBandWrapper(Band b, RehearsalRequest rehearsalRequest)
        {
            Band = b;
            RehearsalRequest = rehearsalRequest;
        }

        public AddBandWrapper(Band b, AppearanceRequest appearanceRequest)
        {
            Band = b;
            AppearanceRequest = appearanceRequest;
        }
    }
}
