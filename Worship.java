import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Worship {

    Map<String, Entity> mEntities = new HashMap<>();
    //List<Map<String, String>> mEntityBuffer = new LinkedList<>();
    Map<String, Map<String, String>> mEntityBuffer = new HashMap<>();

    String mCurrentEntity;
    int mNumOfLines = 0;

    public static void main(String[] args) throws Exception {
        Worship w = new Worship();
        w.readFile();
    }

    private void readFile() throws java.io.IOException {
        File file = new File("RulesMD.ini");

        if (!file.exists()) {
            throw new java.io.IOException();
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int lineNum = 0;

        while ((line = reader.readLine()) != null) {
            lineNum++;
            line = line.trim();
            line = removeComments(line);
            this.parseLine(line);
        }

        reader.close();
    }

    private void parseLine(String line) {
        mNumOfLines++;
        int equalSign;
        if (line.startsWith("[")) {
            int rightBracket = line.indexOf(']');
            if (rightBracket < 0) {
                throw new RuntimeException("Line start with '[' without ending ']'");
            }
            else if (rightBracket == 1) {
                System.out.println("Warning: empty entity name");
            }

            // Find a new entity. Store buffered lines first


            // Move to the new entity
            mCurrentEntity = line.substring(1, rightBracket);

            System.out.println("Get an entity: " + mCurrentEntity);
        }
        else if ((equalSign = line.indexOf('=')) > 0) {
            String key = line.substring(0, equalSign).trim();
            String value = line.substring(equalSign + 1, line.length()).trim();
            System.out.println("Line: " + mNumOfLines + ". Key: " + key + ", value: " + value);


            if (!mEntityBuffer.containsKey(mCurrentEntity)) {
                mEntityBuffer.put(mCurrentEntity, new HashMap<String, String>());
            }

            mEntityBuffer.get(mCurrentEntity).put(key, value);
        }
    }

    private String removeComments(String line) {
        int semicolun = line.indexOf(';');
        String effective;
        if (semicolun > 0) {
            effective = line.substring(0, semicolun).trim();
        }
        else {
            effective = line.trim();
        }
        return effective;
    }

    public static void oldMain(String[] args) throws Exception {

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

    private void peter() {
        ArrayList<Entity> ha = new ArrayList();
        ha.add(new Unit());

    }
}

abstract class Entity {
    public String name;
}

class Unit extends Entity {

}

class Weapon extends Entity {

}

class Warhead extends Entity {

}