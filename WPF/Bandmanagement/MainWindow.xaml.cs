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
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Bandmanagement
{
    /// <summary>
    /// Interaktionslogik für MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
        }

        private void rbLogin_Changed(object sender, RoutedEventArgs e)
        {
            //switch the shown form
            if (this.IsInitialized == true)
            {
                RadioButton checkedRadioButton = this.grLogin.Children.OfType<RadioButton>().FirstOrDefault(r => r.IsChecked == true);

                if (checkedRadioButton.Name == "rbAnmelden")
                {
                    this.grAnmelden.Visibility = Visibility.Visible;
                    this.grBandErstellen.Visibility = Visibility.Hidden;
                }
                else
                {
                    this.grAnmelden.Visibility = Visibility.Hidden;
                    this.grBandErstellen.Visibility = Visibility.Visible;
                }
            }
        }

        private void btnAnmelden_Click(object sender, RoutedEventArgs e)
        {
            //check data (if band exists and so) and try to login or print error
        }

        private void btnBandErstellen_Click(object sender, RoutedEventArgs e)
        {
            //check data and if correct: save new band to sql database
        }
    }
}
