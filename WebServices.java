package networking;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 *
 * @author MCA239
 */
class Consumer{
    HttpURLConnection connection = null;
    void createConnection(String method, String url1) throws MalformedURLException, IOException{
        URL url;
        url = new URL(url1);
        connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setRequestMethod(method); // by default it is GET
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");
    }
    void getHeaderInfo(){
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        headerFields.entrySet().stream().forEach((entrySet) -> {
            String key = entrySet.getKey();
            List<String> value = entrySet.getValue();
            System.out.println(key + " : " + value);
        });
        System.out.println("123"+connection.getRequestProperty("Content-Type"));
    }
    void getData() throws IOException{
        if(connection != null){
            connection.connect();
            if(connection.getResponseCode() == 200){
                String line = "";
                Scanner sc = new Scanner(connection.getInputStream(), "UTF-8");
                while(sc.hasNextLine()){
                    line +=sc.next();
                }
                //parse json
                JSONObject jsonobject = new JSONObject(line);
                JSONArray jsarr = jsonobject.getJSONArray("data");
                for (int i = 0; i < jsarr.length(); i++) {
                    JSONObject userObj = jsarr.getJSONObject(i);
                    String name = userObj.getString("first_name") + " " + userObj.getString("last_name");                    
                    System.out.println(name);
                }
            }
             
            //getHeaderInfo();
        }
    }
    void postData() throws IOException{
        if(connection != null){
            connection.setDoOutput(true); //compulsory property
            connection.connect();
            JSONObject jo = new JSONObject();
            jo.put("name", "Baburao");
            jo.put("job", "Actor");
            OutputStream os = connection.getOutputStream();
            os.write(jo.toString().getBytes());
            os.close();
            if(connection.getResponseCode() == 201){
                String line = "";
                Scanner sc = new Scanner(connection.getInputStream(), "UTF-8");
                while(sc.hasNextLine()){
                    line +=sc.next();
                }
                JSONObject userObj =  new JSONObject(line);
                String data = userObj.getString("id") + " " + userObj.getString("createdAt");                    
                System.out.println(data);
            }
        }
    }
}
public class WebServices {
    public static void main(String []p) throws IOException{
       Consumer c = new Consumer();
        //c.createConnection("GET","https://reqres.in/api/users?per_page=10");
        c.createConnection("POST","https://reqres.in/api/users");
        c.postData();
    }
}
