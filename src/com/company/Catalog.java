package com.company;

import java.util.ArrayList;

public class Catalog extends StoredObject {
    private ArrayList<File> files = new ArrayList<>();
    private ArrayList<Catalog> catalogs = new ArrayList<>();

    public Catalog(String name) {
        super(name);
    }

    public Catalog(StoredObject storedObject) {
        super(storedObject);
    }

    public Catalog(StoredObject storedObject,ArrayList<File> files, ArrayList<Catalog> catalogs) {
        super(storedObject);
        this.files = files;
        this.catalogs = catalogs;
    }
    public Catalog(String name, ArrayList<File> files, ArrayList<Catalog> catalogs)
    {
        super(name);
        this.files = files;
        this.catalogs = catalogs;
    }

    public Catalog(String name, boolean haveChild, int startX, int startY) {
        super(name, haveChild, startX, startY);

    }

    public Catalog getCatalog(Catalog child) {
        for (Catalog cat : catalogs) {
            if(cat.equals(child)){
                return cat;
            }
        }
        return null;
    }

    public File getFile(File child) {
        for (File f : files) {
            if(f.equals(child)){
                return f;
            }
        }
        return null;
    }



    public boolean addFile(File addFile) {
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            if (addFile.getName().equals(file.getName())) {
                return false;
            }
        }
        files.add(addFile);
        return true;
    }

    public boolean addCatalog(Catalog addedCatalog) {
        for (int i = 0; i < catalogs.size(); i++) {
            Catalog catalog = catalogs.get(i);
            if (addedCatalog.getName().equals(catalog.getName())) {
                return false;
            }
        }
        catalogs.add(addedCatalog);
        return true;
    }

    public boolean deleteObject(StoredObject storedObject) {
        if (!storedObject.isHaveChild()) {
            return deleteFile((File) storedObject);
        } else {
            return deleteCatalog((Catalog) storedObject);
        }
    }

    public boolean deleteFile(File f) {
        for (int m = 0; m < files.size(); m++) {
            File file = files.get(m);
            if (f.getName().equals(file.getName())) {
                files.remove(f);
                return true;
            }
        }
        return false;
    }


    public boolean deleteCatalog(Catalog cat) {
        for (int m = 0; m < catalogs.size(); m++) {
            Catalog catalog = catalogs.get(m);
            if (cat.getName().equals(catalog.getName())) {
                catalogs.remove(cat);

                return true;
            }
        }

        return false;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public ArrayList<Catalog> getCatalogs() {
        return catalogs;
    }

    public String getName() {
        return super.toString();
    }



    public int getFullSize() {
        int fullSize = 0;

        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            fullSize += file.getSize();

        }
        for (int i = 0; i < catalogs.size(); i++) {
            Catalog catalog = catalogs.get(i);
            fullSize += catalog.getFullSize();
        }
        return fullSize;
    }
}
