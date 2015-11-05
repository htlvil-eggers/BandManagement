using System;
using System.Collections.Generic;
using System.Data.OleDb;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SpatialClient.Model
{
    public class DatabaseOracle
    {
        private static OleDbConnection myOleDbConnection = null;

        public DatabaseOracle() { }

        public void Connect()
        {
            try
            {
                //ip extern:	212.152.179.117  ---  ip intern:	192.168.128.151
                String connectionString = "Provider=OraOLEDB.Oracle; Data Source=192.168.128.151/ora11g; User Id=d5bhifs25;Password=edelBlech;OLEDB.NET=True;";

                myOleDbConnection = new OleDbConnection(connectionString);
                myOleDbConnection.Open();
            }
            catch (System.Exception _e)
            {
                throw new Exception("Connect-Error ", _e);
            }
        }

        public void Close()
        {
            myOleDbConnection.Close();
        }

        public OleDbConnection getMyOleDbConnection()
        {
            return myOleDbConnection;
        }
    }
}
