using Bandmanagement.Model;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace Bandmanagement.View
{
    /// <summary> 
    /// Interaktionslogik für BandmemberManagement.xaml
    /// </summary>
    public partial class BandmemberManagement : Window
    {
        public Band currentBand;

        public BandmemberManagement(Band signedInBand)
        {
            InitializeComponent();

            currentBand = signedInBand;
            DisplaySignedInData();
        }

        private void DisplaySignedInData()
        {
            this.lblDisplaySignedInUsername.Content = this.currentBand.Leader.Username;
            this.lblDisplaySignedInBand.Content = this.currentBand.Name;            
        }
    }
}
