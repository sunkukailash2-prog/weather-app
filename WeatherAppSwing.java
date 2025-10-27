import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class WeatherAppSwing extends JFrame {

    private JTextField cityField;
    private JTextArea resultArea;
    private JButton fetchButton;

    private final String API_KEY = "YOUR_API_KEY_HERE"; // Replace with your OpenWeatherMap API key

    public WeatherAppSwing() {
        setTitle("Real-Time Weather App");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        cityField = new JTextField(20);
        fetchButton = new JButton("Get Weather");

        topPanel.add(new JLabel("Enter city names (comma separated):"));
        topPanel.add(cityField);
        topPanel.add(fetchButton);

        add(topPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.CENTER);

        // Button action
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cities = cityField.getText().trim();
                if (!cities.isEmpty()) {
                    resultArea.setText(""); // Clear previous results
                    String[] cityList = cities.split(",");
                    for (String city : cityList) {
                        city = city.trim();
                        try {
                            String weatherData = getWeather(city);
                            resultArea.append(weatherData + "\n\n");
                        } catch (Exception ex) {
                            resultArea.append("Error fetching weather for " + city + "\n\n");
                        }
                    }
                }
            }
        });
    }

    private String getWeather(String city) throws Exception {
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" +
                city + "&units=metric&appid=" + API_KEY;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject json = new JSONObject(response.toString());

        String cityName = json.getString("name");
        JSONObject main = json.getJSONObject("main");
        double temp = main.getDouble("temp");
        int humidity = main.getInt("humidity");
        String weatherDesc = json.getJSONArray("weather").getJSONObject(0).getString("description");

        return String.format("City: %s\nTemperature: %.1f°C\nHumidity: %d%%\nCondition: %s",
                cityName, temp, humidity, weatherDesc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeatherAppSwing app = new WeatherAppSwing();
            app.setVisible(true);
        });
    }
}
