import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;

public class Worship {
    public static void main(String[] args) throws Exception {

        File file = new File("RulesMD.ini");
        assert(file.exists());

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int lineNum = 0;

        try {
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim().toLowerCase();

                if (line.startsWith("verses=")) {
                    int equalSign = line.indexOf('=');
                    int semicolumn = line.indexOf(';');
                    String effective;

                    if (semicolumn > 0) {
                        effective = line.substring(equalSign + 1, semicolumn).trim();
                    }
                    else {
                        effective = line.substring(equalSign + 1).trim();
                    }
                    String[] verses = effective.split(",");
                    assert(verses.length == 9);

                    int [] damages = new int[9];
                    for (int i = 0; i < 9; i++) {
                        int percentSign = verses[i].indexOf("%");
                        if (percentSign > 0) {
                            damages[i] = Integer.valueOf(verses[i].substring(0, percentSign));
                        }
                        else {
                            damages[i] = Integer.valueOf(verses[i]);
                        }
                    }
                    int heavy = damages[5];
                    int medium = damages[4];
                    int light = damages[3];
                    if (heavy > medium) {
                        System.out.printf("---- Heavy %d takes more damages than medium %d at line %d\n",
                                heavy, medium, lineNum);
                    }
                    else if (heavy < medium) {
                        System.out.printf("++++ Heavy %d takes less damages than medium %d at line %d\n",
                                heavy, medium, lineNum);
                    }

                    if (light <= medium && medium <= heavy && light < heavy) {
                        System.out.printf("???? Ascending %d, %d, %d at line %d\n",
                                light, medium, heavy, lineNum);
                    }

                    if (light < medium && medium > heavy) {
                        System.out.printf(">>>> Peak %d, %d, %d at line %d\n",
                                light, medium, heavy, lineNum);
                    }

                    if (light > medium && medium < heavy) {
                        System.out.printf("<<<< Valley at %d, %d, %d at line %d\n",
                                light, medium, heavy, lineNum);
                    }
                }
            }
        }
        finally {
            reader.close();
        }

    }
}

abstract class Entity {
    public String name;
}

class Unit extends Entity {

}