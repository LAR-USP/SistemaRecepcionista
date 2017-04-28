/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usp.lar.lara.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.DataStoreFactory;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 * @author tarcisio
 */
public class CalendarSearch {
   /** Application name. */
    private static final String APPLICATION_NAME = "LARA";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/LARA-calendar");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart
     */
    private static final List<String> SCOPES =
        Arrays.asList(CalendarScopes.CALENDAR_READONLY);

    // Build a new authorized API client service.
    // Note: Do not confuse this class with the
    //   com.google.api.services.calendar.model.CalendarSearch class.
    private static com.google.api.services.calendar.Calendar service;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
            CalendarSearch.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized CalendarSearch client service.
     * @return an authorized CalendarSearch client service
     * @throws IOException
     */
    public static com.google.api.services.calendar.Calendar
        getCalendarService() throws IOException {
        Credential credential = authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    static{
        try{
            service = CalendarSearch.getCalendarService();
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static JsonObject getList(String pergunta) throws IOException {
        String answer = "";
        ArrayList<String> tokens = new ArrayList(Arrays.asList(pergunta.split(" ")));
        JsonObject rv= new JsonObject();
        String date = "";
        String time = "";
        ArrayList<String> date_keywords = new ArrayList(Arrays.asList("dia", "mes", "mês", "ano", "semana", "hoje", "amanhã", "amanha"));
        ArrayList<String> time_keywords = new ArrayList(Arrays.asList("manha", "manhã", "tarde", "noite"));
        Iterator<String> it = tokens.iterator();
        while(it.hasNext()){
            String token = it.next();
            if(date_keywords.stream().anyMatch(token::equalsIgnoreCase)){
                date = token;
            } else if(time_keywords.stream().anyMatch(token::equalsIgnoreCase)){
                time = token;
            }
        }

        // Resultado
        List<Event> items = CalendarSearch.search(date, time).getItems();
        if(!items.isEmpty()) {
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                answer += event.getSummary() + " (" + start + ") <br />";
            }
        }
        
        rv.addProperty("answer", answer);
        rv.addProperty("entity", "");
        rv.add("properties", new JsonArray());
        return rv;
    } 

    static private Events search(String date, String time) throws IOException{

        Events events = null;
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0); 
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date d1 = null;
        Date d2 = null;
        // Dia:
        if(date.equalsIgnoreCase("hoje")){
            d1 = c.getTime();
            c.add(Calendar.DAY_OF_MONTH, 1);
            d2 = c.getTime();
        }else if(date.equalsIgnoreCase("amanhã") || date.equalsIgnoreCase("amanha")){
            c.add(Calendar.DAY_OF_MONTH, 1);
            d1 = c.getTime();
            c.add(Calendar.DAY_OF_MONTH, 1);
            d2 = c.getTime();
        } else if(date.equalsIgnoreCase("semana")){
            d1 = c.getTime();
            c.add(Calendar.DAY_OF_MONTH, 7);
            d2 = c.getTime();
        } else if(date.equalsIgnoreCase("mes") || date.equalsIgnoreCase("mês")){
            d1 = c.getTime();
            c.add(Calendar.MONTH, 1);
            d2 = c.getTime();
        } else if(date.equalsIgnoreCase("ano")){
            d1 = c.getTime();
            c.add(Calendar.YEAR, 1);
            d2 = c.getTime();
        }

        events = CalendarSearch.service.events().list("primary")
            .setTimeMin(new DateTime(d1))
            .setTimeMax(new DateTime(d2))
            .setOrderBy("startTime")
            .setMaxResults(100)
            .setSingleEvents(true)
            .execute();

        // Horário
        if(time.equalsIgnoreCase("manhã") || time.equalsIgnoreCase("manha")){
            events.setItems(CalendarSearch.filterByHour(events, 4, 12));
        } else if(time.equalsIgnoreCase("tarde")){
            events.setItems(CalendarSearch.filterByHour(events, 12, 20));
        } else if(time.equalsIgnoreCase("noite")){
            events.setItems(CalendarSearch.filterByHour(events, 20, 4));
        }

        return events;
    }

    static private ArrayList<Event> filterByHour(Events events, int min, int max){
        ArrayList<Event> event_list = new ArrayList(events.getItems());
        ArrayList<Event> new_list = new ArrayList();
        Iterator<Event> item = event_list.iterator();
        while(item.hasNext()){
            Event e = item.next();
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
            c.setTimeInMillis(e.getStart().getDateTime().getValue());
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if(max > min && min <= hour && hour < max){
                new_list.add(e);
            } else if(min > max && ((min <= hour && hour <= 24) || (0 <= hour && hour <= max))){
                new_list.add(e);
            }
        }
        return new_list;
    }
}
