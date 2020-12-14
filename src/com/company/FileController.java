package com.company;

import javax.swing.*;
import java.util.ArrayList;


public class FileController {
    private final TreeManager treeManager;
    private final Form form;
    private ArrayList<StoredObject> moveObjectTree;
    private StoredObject moveObject;
    private StoredObject moveObjectForMove;
    private final Disk disk;
    private final FileSystem fileSystem;
    private String emptyString = "";

    public FileController(Form form, Disk disk, FileSystem fileSystem, TreeManager treeManager) {
        this.form = form;
        this.disk = disk;
        this.fileSystem = fileSystem;
        this.treeManager = treeManager;
    }


    public void addCatalog() {
        String name = "just";

        boolean circle = true;
        while (circle) {
            name = JOptionPane.showInputDialog(form.frame, "Введите имя каталога");
            if (name == null) {
                return;
            } else if (name.equals(emptyString)) {
                JOptionPane.showMessageDialog(form.frame, "Пустое имя некорректно", "Создание каталога", JOptionPane.INFORMATION_MESSAGE);
            } else {
                circle = false;
            }
        }

        StoredObject parent = treeManager.getPickObject();

        if (parent == null) {
            JOptionPane.showMessageDialog(form.frame, "Выберете место расположения каталога", "Создание каталога", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else if (parent.isHaveChild()) {
            Catalog parentCatalog = (Catalog) parent;
            Catalog newCatalog = new Catalog(name);
            JOptionPane.showMessageDialog(form.frame, fileSystem.addCatalog(parentCatalog, newCatalog), "Создание каталога", JOptionPane.INFORMATION_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(form.frame, "Выберете место расположения каталога, а не файл", "Создание каталога", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

    }


    public void addFile() {


        String name = "just";
        int leftSize = disk.getFreeSegment() * disk.getSegmentSize();

        if (leftSize == 0) {
            JOptionPane.showMessageDialog(form.frame, "Недостаточно места", "Добавление файла", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        boolean circle = true;
        while (circle) {
            name = JOptionPane.showInputDialog(form.frame, "Введите имя файла");
            if (name == null) {
                return;
            } else if (name.equals(emptyString)) {
                JOptionPane.showMessageDialog(form.frame, "Пустое имя некорректно", "Создание файла", JOptionPane.INFORMATION_MESSAGE);
            } else {
                circle = false;
            }
        }

        int sizeFile = 1;

        circle = true;

        while (circle) {
            try {
                sizeFile = Integer.parseInt(JOptionPane.showInputDialog(form.frame, "Введите размер файла от " + 1 +
                        " до " + leftSize));
                if (sizeFile < 1 || sizeFile > leftSize) {
                    throw new Exception();
                } else {
                    circle = false;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(form.frame, "Неверные данные", "Добавление файла", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        StoredObject parent = treeManager.getPickObject();

        if (parent == null) {
            JOptionPane.showMessageDialog(form.frame, "Выберете место расположения файла", "Создание файла", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else if (parent.isHaveChild()) {
            Catalog parentCatalog = (Catalog) parent;
            File newFile = new File(name, sizeFile);
            JOptionPane.showMessageDialog(form.frame, fileSystem.addFile(parentCatalog, newFile), "Создание файла", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(form.frame, "Выберете место расположения файла, а не файл", "Создание файла", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

    }

    public void deleteObject(StoredObject storedObject) {
        if (storedObject == null) {
            JOptionPane.showConfirmDialog(form.frame, "Выберите объект, который хотите удалить",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION);
            return;
        } else if (storedObject.getLevel() == 0) {
            JOptionPane.showConfirmDialog(form.frame, "Нельзя удалить корневой каталог",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION);
            return;
        }

        int result = JOptionPane.showConfirmDialog(form.frame, "Удалить объект " + storedObject.getName() + "?",
                "Удаление объекта", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            Catalog parent = treeManager.getParentPickObject(storedObject);
            JOptionPane.showMessageDialog(form.frame, fileSystem.deleteObject(parent, storedObject), "Удаление объекта", JOptionPane.INFORMATION_MESSAGE);

        }
    }

    public void copy(StoredObject pickObject) {

        if (pickObject == null) {
            JOptionPane.showConfirmDialog(form.frame, "Выберите объект, который нужно скопировать",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION);
            return;
        }
        else if(pickObject.getLevel() == 0) {
            JOptionPane.showConfirmDialog(form.frame, "Нельзя скопировать корневой каталог",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION);
            return;
        }

        moveObjectTree = treeManager.getCopyTree(pickObject);
        StoredObject storedObject = fileSystem.copyObject(treeManager.getParentPickObject(pickObject), pickObject); //
        if (storedObject.isHaveChild()) {
            Catalog replace = (Catalog) storedObject;
            moveObjectForMove = replace;
            moveObject = new Catalog(replace, replace.getFiles(), replace.getCatalogs());
        } else {
            File replace = (File) storedObject;
            moveObjectForMove = replace;
            moveObject = new File(storedObject, replace.getSize());
        }

        JOptionPane.showMessageDialog(form.frame, "Объект " + pickObject.getName() + " скопирован ",
                "Копирование", JOptionPane.INFORMATION_MESSAGE);
    }


    public void transfer(StoredObject storedObject) {
        if (storedObject == null) {
            JOptionPane.showConfirmDialog(form.frame, "Выберите место копирования",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION);
            return;
        } else if (!storedObject.isHaveChild()) {
            JOptionPane.showConfirmDialog(form.frame, "Выберите каталог, а не файл",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION);
            return;
        } else if (moveObject == null) {
            JOptionPane.showConfirmDialog(form.frame, "Скопируйте объект",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION);
            return;
        }

        int result = JOptionPane.showConfirmDialog(form.frame, "Вставить объект " + moveObject.getName() + " в каталог " + storedObject.getName() + "?",
                "Вставка объекта", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        int leftSize = disk.getFreeSegment() * disk.getSegmentSize();
        int fullSize;
        if (moveObject.isHaveChild()) {
            Catalog cat = (Catalog) moveObject;
            fullSize = cat.getFullSize();
        } else {
            File file = (File) moveObject;
            fullSize = file.getSize();
        }
        if (fullSize > leftSize) {
            JOptionPane.showMessageDialog(form.frame, "Недостаточно места ",
                    "Копирование", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else if (result == JOptionPane.YES_OPTION) {
            Catalog parentCatalog = (Catalog) storedObject;
            if (moveObject.isHaveChild()) {
                Catalog replace = (Catalog) moveObject;
                Catalog childObject = new Catalog(replace.getName(), replace.getFiles(), replace.getCatalogs());
                JOptionPane.showMessageDialog(form.frame, fileSystem.addCatalog(parentCatalog, childObject, moveObjectTree), "Вставка каталога", JOptionPane.INFORMATION_MESSAGE);
            } else {
                File replace = (File) moveObject;
                File childObject = new File(replace.getName(), replace.getSize());
                JOptionPane.showMessageDialog(form.frame, fileSystem.addFile(parentCatalog, childObject), "Вставка файла", JOptionPane.INFORMATION_MESSAGE);
            }

        }
    }

    public void move(StoredObject storedObject) {
        if (storedObject == null) {
            JOptionPane.showConfirmDialog(form.frame, "Выберите место перемещения",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION);
            return;
        } else if (!storedObject.isHaveChild()) {
            JOptionPane.showConfirmDialog(form.frame, "Выберите каталог, а не файл",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION);
            return;
        } else if (moveObject == null || moveObjectForMove == null) {
            JOptionPane.showConfirmDialog(form.frame, "Скопируйте объект",
                    "Ошибка", JOptionPane.OK_CANCEL_OPTION);
            return;
        }

        int result = JOptionPane.showConfirmDialog(form.frame, "Переместить объект " + moveObject.getName() + " в каталог " + storedObject.getName() + "?",
                "Перемещение объекта", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            Catalog parentCatalog = (Catalog) storedObject;
            if (moveObjectForMove.isHaveChild()) {
                Catalog replace = (Catalog) moveObjectForMove;
                JOptionPane.showMessageDialog(form.frame, fileSystem.addCatalog(parentCatalog, replace, moveObjectTree, moveObjectForMove), "Вставка каталога", JOptionPane.INFORMATION_MESSAGE);
            } else {
                File replace = (File) moveObjectForMove;
                JOptionPane.showMessageDialog(form.frame, fileSystem.addFile(parentCatalog, replace, moveObjectForMove), "Вставка файла", JOptionPane.INFORMATION_MESSAGE);
            }
            moveObjectForMove = null;
        }
    }

}
