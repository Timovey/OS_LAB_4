package com.company;

import java.awt.*;
import java.util.ArrayList;


public class FileSystem {

    private final TreeManager treeManager;
    private final Disk disk;
    private final Segment[] diskSegment;
    Status status = new Status();

    public FileSystem(Disk disk, TreeManager treeManager) {
        this.disk = disk;
        diskSegment = disk.getSegmentArr();
        for (int i = 0; i < diskSegment.length; i++) {
            diskSegment[i].setSegmentStatus(status.free);
        }
        this.treeManager = treeManager;
    }


    public void pickElement(File file) {
        for (int i = 0; i < diskSegment.length; i++) {
            if (diskSegment[i].getSegmentStatus() == status.pick) {
                diskSegment[i].setSegmentStatus(status.occupied);
            }
        }

        Node node = file.getFirstNode();
        while (node != null) {
            diskSegment[node.getNumber()].setSegmentStatus(status.pick);
            node = node.getNext();
        }
    }

    public void pickDefaultElement() {
        for (int i = 0; i < diskSegment.length; i++) {
            if (diskSegment[i].getSegmentStatus() == status.pick) {
                diskSegment[i].setSegmentStatus(status.occupied);
            }
        }
    }

    public StoredObject copyObject(Catalog parent, StoredObject storedObject) {
        if (storedObject.isHaveChild()) {
            return parent.getCatalog((Catalog) storedObject);
        } else {
            return parent.getFile((File) storedObject);
        }
    }

    public String addCatalog(Catalog parent, Catalog catalog) {
        if (parent.addCatalog(catalog)) {
            treeManager.addCatalog(parent, catalog);
            return "Каталог " + catalog.getName() + "создан в каталоге " + parent.getName();
        }
        return "Такой каталог уже существует в каталоге" + parent.getName();
    }

    public String addCatalog(Catalog parent, Catalog catalog, ArrayList<StoredObject> moveObjectTree) {

        if (parent.addCatalog(catalog)) {
            parent.deleteCatalog(catalog);
            treeManager.addArrayObject(parent, moveObjectTree);
            reissue(parent);
            return "Каталог " + catalog.getName() + "создан в каталоге " + parent.getName();
        }
        return "Такой каталог уже существует в каталоге" + parent.getName();
    }


    public String addCatalog(Catalog parent, Catalog catalog, ArrayList<StoredObject> moveObjectTree, StoredObject moveObjectForMove) {

        if (parent.addCatalog(catalog)) {
            deleteObject(treeManager.getParentPickObject(catalog), moveObjectForMove);
            parent.deleteCatalog(catalog);
            treeManager.addArrayObject(parent, moveObjectTree);
            reissue(parent);
            return "Каталог " + catalog.getName() + "создан в каталоге " + parent.getName();
        }
        return "Такой каталог уже существует в каталоге" + parent.getName();
    }


    public void reissue(Catalog catalog) {
        for (File file : catalog.getFiles()) {
            int fullSize;
            fullSize = file.getSize();

            int fileSectorSize = fullSize / disk.getSegmentSize();
            if (fileSectorSize == 0) {
                fileSectorSize = 1;
            }
            file.setFirstNode(addInDisk(fileSectorSize, new Node()));
        }


        for (Catalog cat : catalog.getCatalogs()) {
            cat = new Catalog(cat.getName(), cat.getFiles(), cat.getCatalogs());
            reissue(cat);
        }

    }

    public String deleteObject(Catalog parent, StoredObject storedObject) {

        if (parent.deleteObject(storedObject)) {
            clearMemory(storedObject);
            treeManager.deleteObject(storedObject);
            return "Объект " + storedObject.getName() + " удален";
        } else {
            if (storedObject.isHaveChild()) {
                addCatalog(parent, (Catalog) storedObject);
            } else {
                addFile(parent, (File) storedObject);
            }
        }
        return "Не удалось удалить объект " + storedObject.getName();
    }

    public void clearMemory(StoredObject storedObject) {
        if (storedObject.isHaveChild()) {
            Catalog catalog = (Catalog) storedObject;
            int countCatalogs = catalog.getCatalogs().size();
            int countFiles = catalog.getFiles().size();

            while (countCatalogs > 0 || countFiles > 0) {
                for (int i = 0; i < catalog.getCatalogs().size(); i++) {
                    clearMemory(catalog.getCatalogs().get(i));
                    countCatalogs--;
                }
                for (int i = 0; i < catalog.getFiles().size(); i++) {
                    deleteFromDisk(catalog.getFiles().get(i));
                    countFiles--;
                }
            }
        } else {
            File file = (File) storedObject;
            deleteFromDisk(file);
        }

    }

    public void deleteFromDisk(File file) {
        Node node = file.getFirstNode();

        while (node != null) {
            diskSegment[node.getNumber()].setSegmentStatus(status.free);
            disk.addFreeSegment();
            node = node.getNext();
        }

    }

    public String addFile(Catalog catalog, File file, StoredObject moveObjectForMove) {

        int fullSize;
        fullSize = file.getSize();

        int fileSectorSize = fullSize / disk.getSegmentSize();
        if (fileSectorSize == 0) {
            fileSectorSize = 1;
        }

        if (catalog.addFile(file)) {
            deleteObject(treeManager.getParentPickObject(catalog), moveObjectForMove);
            file.setFirstNode(addInDisk(fileSectorSize, new Node()));
            treeManager.addFile(catalog, file);
            return "Файл " + file.getName() + " успешно создан в каталоге " + catalog.getName();
        }




        return "Файл " + file.getName() + " уже существует в каталоге " + catalog.getName();

    }

    public String addFile(Catalog catalog, File file) {

        int fullSize;
        fullSize = file.getSize();

        int fileSectorSize = fullSize / disk.getSegmentSize();
        if (fileSectorSize == 0) {
            fileSectorSize = 1;
        }
        if (disk.getFreeSegment() < fileSectorSize) {
            return "Недостаточно места на диске для создания данного файла";
        }
        if (catalog.addFile(file)) {
            file.setFirstNode(addInDisk(fileSectorSize,new Node()));
            treeManager.addFile(catalog, file);
            return "Файл " + file.getName() + " успешно создан в каталоге " + catalog.getName();

        }

        return "Файл " + file.getName() + " уже существует в каталоге " + catalog.getName();

    }

    private Node addInDisk(int fileSectorSize, Node node) {
        int index;
        boolean added = false;
        while (fileSectorSize > 0 && !added) {
            index = getRandomNumber(disk.getSegmentAmount() - 1);
            if (diskSegment[index].getSegmentStatus() == status.free) {
                added = true;
                disk.deleteFreeSegment();
                node.setNumber(index);
                diskSegment[index].setSegmentStatus(status.occupied);
                fileSectorSize--;
                Node newNodeNext = new Node();
                node.setNext(addInDisk(fileSectorSize, newNodeNext));

                return node;
            }
        }
        return null;
    }


    private int getRandomNumber(int Numb) {
        return (int) (Math.random() * (Numb + 1));
    }


    public void draw(Graphics g) {
        int scale = 15;

        for (int i = 0; i <= diskSegment.length / 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (i * 30 + j >= diskSegment.length) {
                    return;
                }

                int status = diskSegment[i * 30 + j].getSegmentStatus();
                g.setColor(checkStatus(status));
                g.fillRect(15 + j * scale, 15 + i * scale, scale, scale);
                g.setColor(Color.BLACK);
                g.drawRect(15 + j * scale, 15 + i * scale, scale, scale);
            }
        }
    }

    private Color checkStatus(int status) {
        Color color = new Color(139, 150, 161);
        if (status == this.status.free) {

        } else if (status == this.status.occupied) {
            color = new Color(70, 60, 214);
        } else {
            color = new Color(222, 55, 39);
        }
        return color;
    }
}
