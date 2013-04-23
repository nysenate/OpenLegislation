package gov.nysenate.openleg.scripts;

import gov.nysenate.services.model.District;
import gov.nysenate.services.model.Office;
import gov.nysenate.services.model.Senator;
import gov.nysenate.services.model.Social;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;

public class UpdateJSON extends BaseScript{

    public static void main(String[] args) throws Exception
    {
        new UpdateJSON().run(args);
    }

    public void execute(CommandLine opts) throws IOException
    {
        File baseDir = new File("/home/graylinkim/projects/nysenate/OpenLegislation/src/main/resources/data/");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(Feature.INDENT_OUTPUT, true);

        update(new File(baseDir,"districts-2009"), new File(baseDir,"senators/2009"), mapper);
        update(new File(baseDir,"districts"), new File(baseDir,"senators/2011"), mapper);
    }

    public void update(File sourceDir, File destDir, ObjectMapper mapper) throws IOException
    {
        FileUtils.forceMkdir(destDir);
        for (File file : FileUtils.listFiles(sourceDir, null, false)) {
            JsonNode json = mapper.readTree(file);

            District district = new District();
            district.setUrl(json.get("districtUrl").asText());
            String number = json.get("district").getTextValue().split(" ")[3];
            district.setNumber(Integer.valueOf(number));

            ArrayList<Office> offices = new ArrayList<Office>();
            Iterator<JsonNode> jsonOffices = json.get("senator").get("offices").getElements();
            while (jsonOffices.hasNext()) {
                Office office = new Office();
                JsonNode jsonOffice = jsonOffices.next();
                office.setStreet(jsonOffice.get("street").asText());
                office.setCity(jsonOffice.get("city").asText());
                office.setPostalCode(jsonOffice.get("zip").asText());

                office.setProvince("NY");
                office.setProvinceName("New York");
                office.setCountry("us");
                office.setCountryName("United States");

                office.setLatitude(jsonOffice.get("lat").asDouble());
                office.setLongitude(jsonOffice.get("lon").asDouble());
                office.setName(jsonOffice.get("officeName").asText());
                office.setPhone(jsonOffice.get("phone").asText());
                office.setFax(jsonOffice.get("fax").asText());
                offices.add(office);
            }

            Social social = new Social();
            social.setFacebook(json.get("senator").get("social").get("faceBook").asText());
            social.setTwitter(json.get("senator").get("social").get("twitter").asText());
            social.setYoutube(json.get("senator").get("social").get("youtube").asText());
            social.setFlickr(json.get("senator").get("social").get("flickr").asText());

            Senator senator = new Senator();
            senator.setSocial(social);
            senator.setOffices(offices);
            senator.setDistrict(district);
            senator.setEmail(json.get("senator").get("contact").asText());
            senator.setUrl(json.get("senator").get("url").asText());
            senator.setImageUrl(json.get("senator").get("imageUrl").asText());

            // The old files have some weird encoding issues so you have to manually remove the é and put it back again here.
            String name = json.get("senator").get("name").getTextValue();
            if (name.equals("Martin Malave Dilan")) {
                name = "Martin Malavé Dilan";
            } else if (name.equals("Jose Peralta")) {
                name = "José Peralta";
            } else if (name.equals("Jose M. Serrano")) {
                name = "José M. Serrano";
            }
            senator.setName(name);
            if (senator.getLastName().isEmpty()) {
                String[] nameParts = name.split(" ");
                String lastName = nameParts[nameParts.length-1];
                senator.setLastName(lastName);
            }
            if (senator.getShortName().isEmpty()) {
                senator.setShortName(senator.getLastName());
            }

            File destFile = new File(destDir, district.getNumber()+".json");
            System.out.println("Writing "+file.getName()+" to "+destFile.getAbsolutePath());
            mapper.writeValue(destFile, senator);
        }
    }

}
