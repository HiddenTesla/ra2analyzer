import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Worship {

    private Map<String, Entity> mEntities = new HashMap<>();
    private Map<String, Map<String, String>> mEntityBuffer = new HashMap<>();

    private String mCurrentEntity;
    private int mNumOfLines = 0;

    public static void main(String[] args) throws Exception {
        Worship w = new Worship();
        w.readFile();

        System.out.println("Read file done");

        w.traverse();
    }

    public void readFile() throws java.io.IOException {
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

        storeEntity();
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
            String oldEntity = mCurrentEntity;
            storeEntity();

            // Move to the new entity
            mCurrentEntity = line.substring(1, rightBracket);

            //System.out.printf("Moving from [%s] to [%s]\n", oldEntity, mCurrentEntity);
        }
        else if ((equalSign = line.indexOf('=')) > 0) {
            String key = line.substring(0, equalSign).trim();
            String value = line.substring(equalSign + 1, line.length()).trim();
            //System.out.println("Line: " + mNumOfLines + ". Key: " + key + ", value: " + value);

            if (!mEntityBuffer.containsKey(mCurrentEntity)) {
                mEntityBuffer.put(mCurrentEntity, new HashMap<String, String>());
            }

            mEntityBuffer.get(mCurrentEntity).put(key, value);
        }
    }

    private void storeEntity() {
        Map<String, String> currentBuffer = mEntityBuffer.get(mCurrentEntity);
        //System.out.printf("Storing [%s]\n", mCurrentEntity);

        if (currentBuffer == null)
            return;

        for (String key: currentBuffer.keySet()) {
            String value = currentBuffer.get(key);
            //System.out.printf("%s = %s\n", key, value);

            if (key.equalsIgnoreCase("strength")) {
                Unit unit = new Unit();
                unit.title = mCurrentEntity;
                unit.name = currentBuffer.get("Name");
                unit.strength = value;
                unit.primary = currentBuffer.get("Primary");
                unit.secondary = currentBuffer.get("Secondary");
                if (unit.name == null) {
                    System.out.printf("Unit [%s] has no name\n", mCurrentEntity);
                    unit.name = unit.title;
                }

                mEntities.put(mCurrentEntity, unit);
            }
            else if (key.equalsIgnoreCase("warhead")) {
                Weapon weapon = new Weapon();
                weapon.title = mCurrentEntity;
                weapon.warhead =  currentBuffer.get("Warhead");
                if (weapon.warhead == null) {
                    System.out.printf("Weapon [%s] has no warhead\n", mCurrentEntity);
                    throw new NullPointerException(mCurrentEntity);
                }
                mEntities.put(mCurrentEntity, weapon);
            }
            else if (key.equalsIgnoreCase("verses")) {
                Warhead warhead = new Warhead();
                warhead.title = mCurrentEntity;
                warhead.verses = currentBuffer.get("Verses");
                if (warhead.verses == null) {
                    System.out.printf("Warhead [%s] has no verses\n", mCurrentEntity);
                    throw new NullPointerException(mCurrentEntity);
                }
                mEntities.put(mCurrentEntity, warhead);
            }
        }
    }

    private void traverse() {
        for (String title: mEntities.keySet()) {
            Entity entity = mEntities.get(title);
            if (!(entity instanceof Unit))
                continue;

            Unit unit = (Unit)entity;

            Weapon primary = (Weapon)mEntities.get(unit.primary);
            Weapon secondary = (Weapon)mEntities.get(unit.secondary);

            if (primary != null) {
                if (secondary != null) {
                    System.out.printf("'%s' has primary weapon [%s] and secondary weapon [%s]\n",
                            unit.name, primary.title, secondary.title);
                }
                else{
                    System.out.printf("'%s' has primary weapon [%s]\n", unit.name, primary.title);
                }
            }
            else {
                //System.out.printf("'%s' has no primary weapon\n", unit.name);
                if (secondary != null) {
                    System.out.printf("'%s' has no primary weapon but secondary weapon [%s]\n", unit.name, unit.secondary);
                    throw new IllegalArgumentException();
                }
            }

            //System.out.printf("Unit [%s] has strength %s\n", name, unit.strength);
        }
    }

    private String removeComments(String line) {
        int semicolon = line.indexOf(';');
        String effective;
        if (semicolon >= 0) {
            effective = line.substring(0, semicolon).trim();
        }
        else {
            effective = line.trim();
        }
        return effective;
    }
}

abstract class Entity {
    public String title;
}

class Unit extends Entity {
    public String name;
    public String strength;
    public String primary;
    public String secondary;
}

class Weapon extends Entity {
    public String warhead;
}

class Warhead extends Entity {
    public String verses;
}