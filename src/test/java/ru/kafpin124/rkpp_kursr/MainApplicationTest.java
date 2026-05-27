package ru.kafpin124.rkpp_kursr;

import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import ru.kafpin124.rkpp_kursr.contoller.LoginController;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.testfx.util.WaitForAsyncUtils.waitFor;

public class MainApplicationTest extends ApplicationTest {

    private final String demoLogin = "demo";
    private final String demoPassword = "demo";

    @Override
    public void start(Stage stage) throws Exception {
        new MainApplication().start(stage);
    }


    @Test
    public void changeLocaleTest() throws TimeoutException {
//
//        ComboBox<Locale> comboBox = lookup("#languageCombo").query();
//
//        clickOn("#languageCombo").clickOn(".arrow-button");
//        clickOn(comboBox);
        clickOn("#languageCombo");
        clickOn("de_DE");



//        int index = 1;
//        clickOn("#languageCombo");
//
//        for (int i = 0; i <= index; i++) {
//            type(KeyCode.DOWN);
//        }
//
//
//        clickOn("#");

    }



    @Test
    public void demoLoginTest() throws TimeoutException {
        clickOn("#loginField");
        waitFor(3, TimeUnit.SECONDS, () ->
                lookup("#loginField").query().isVisible());
        write(demoLogin);
        clickOn("#passwordField");
        write(demoPassword);
        clickOn("#btnLogin");
    }
}


//
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static waitFor;
//
//public class MainApplicationTest extends ApplicationTest {
//    private final StudentDao dao = new StudentDao();
//    private final String uniqueFirstName = "Тест";
//    private final String uniqueLastName = "Тестов";
//    private final String age = "25";
//    private final List<Student> studentsToClean = new ArrayList<>();
//
//    @Override
//    public void start(Stage stage) throws Exception {
//        new MainApplication().start(stage);
//    }
//
//    @AfterEach
//    void cleanUp() {
//        for (Student s : studentsToClean) {
//            Student existing = dao.findByLastName(s.getLastName()).stream()
//                    .filter(st -> st.getFirstName().equals(s.getFirstName()))
//                    .findFirst()
//                    .orElse(null);
//            if (existing != null) {
//                dao.delete(existing);
//            }
//        }
//        studentsToClean.clear();
//
//    }
//
//    @SneakyThrows
//    @Test
//    @DisplayName("Добавление студента")
//    void testAddStudent() {
//        clickOn("#addButton");
//        waitFor(3, TimeUnit.SECONDS, () ->
//                lookup("#tfName").query().isVisible());
//        //или
//        waitFor(5, TimeUnit.SECONDS, () ->
//                lookup("Ok").tryQuery().isPresent());
//
//        clickOn("#tfName").write(uniqueFirstName);
//        clickOn("#tfLastName").write(uniqueLastName);
//
//        clickOn("#tfAge")
//                .press(KeyCode.COMMAND) //KeyCode.CONTROL
//                .press(KeyCode.A)
//                .release(KeyCode.A)
//                .release(KeyCode.COMMAND) //KeyCode.CONTROL
//                .write(age);
//        clickOn("Ok");
//
//        sleep(200, TimeUnit.MILLISECONDS);
//
//        TableView<Student> tableView = lookup("#studentsTable").query();
//
//        boolean found = false;
//        for (Student s : tableView.getItems()) {
//            if (s.getFirstName().equals(uniqueFirstName) &&
//                    s.getLastName().equals(uniqueLastName) &&
//                    s.getAge() == Integer.parseInt(age)) {
//                found = true;
//                break;
//            }
//        }
//        assertThat(found).isTrue();
//
//        Student created = dao.findByLastName(uniqueLastName).stream()
//                .filter(s -> s.getFirstName().equals(uniqueFirstName))
//                .findFirst()
//                .orElse(null);
//        if (created != null) studentsToClean.add(created);
//    }
//
//    @SneakyThrows
//    @Test
//    @DisplayName("Удаление студента")
//    void testDeleteStudent() {
//        Student student = new Student(0, uniqueFirstName, uniqueLastName,
//                Integer.parseInt(age));
//        dao.save(student);
//        studentsToClean.add(student);
//
//        clickOn("#clearButton");
//        Thread.sleep(500);
//
//        clickOn(uniqueLastName);
//
//        clickOn("#deleteButton");
//
//        Thread.sleep(500);
//
//        TableView<Student> tableView = lookup("#studentsTable").query();
//        boolean found = false;
//        for (Student s : tableView.getItems()) {
//            if (s.getFirstName().equals(uniqueFirstName) &&
//                    s.getLastName().equals(uniqueLastName) &&
//                    s.getAge() == Integer.parseInt(age)) {
//                found = true;
//                break;
//            }
//        }
//        assertThat(found).isFalse();
//
//    }
//
//    @SneakyThrows
//    @Test
//    @DisplayName("Редактирование студента")
//    void testEditStudent() {
//        String originalFirstName = "Оригиналимя";
//        String originalLastName = "Оригиналфам";
//        int originalAge = 20;
//        Student student = new Student(0, originalFirstName, originalLastName,
//                originalAge);
//        dao.save(student);
//        studentsToClean.add(student);
//
//        clickOn("#clearButton");
//        Thread.sleep(500); // ждём загрузки
//
//        clickOn(originalLastName);
//        clickOn("#editButton");
//
//        waitFor(3, TimeUnit.SECONDS, () ->
//                lookup("#tfName").tryQuery().isPresent());
//
//        Student newStudent = new Student(0, "Новоеимя", "Новаяфам", 35);
//
//        clickOn("#tfName");
//        clickOn("#tfName");
//        clickOn("#tfName");
//        write(newStudent.getFirstName());
//
//        clickOn("#tfLastName");
//        clickOn("#tfLastName");
//        clickOn("#tfLastName");
//        write(newStudent.getLastName());
//
//        clickOn("#tfAge");
//        clickOn("#tfAge");
//        clickOn("#tfAge");
//        write(String.valueOf(newStudent.getAge()));
//
//        clickOn("Ok");
//
//        Thread.sleep(500);
//
//        TableView<Student> tableView = lookup("#studentsTable").query();
//        boolean foundNew = false;
//        boolean foundOld = false;
//
//        for (Student s : tableView.getItems()) {
//            if (s.getFirstName().equals(newStudent.getFirstName()) &&
//                    s.getLastName().equals(newStudent.getLastName()) &&
//                    s.getAge() == newStudent.getAge()) {
//                foundNew = true;
//            }
//            if (s.getFirstName().equals(originalFirstName) &&
//                    s.getLastName().equals(originalLastName) &&
//                    s.getAge() == originalAge) {
//                foundOld = true;
//            }
//        }
//        assertThat(foundNew).isTrue();
//        assertThat(foundOld).isFalse();
//
//        Student updated =
//                dao.findByLastName(newStudent.getLastName()).stream()
//                        .filter(s ->
//                                s.getFirstName().equals(newStudent.getFirstName()))
//                        .findFirst()
//                        .orElse(null);
//        if (updated != null) {
//            studentsToClean.remove(student);
//            studentsToClean.add(updated);
//        }
//    }
//
//    @SneakyThrows
//    @Test
//    @DisplayName("Поиск студента по фамилии")
//    void testFindButton() {
//        String findLastName = "Смирнов_Нов";
//        String otherLastName = "Иванов_Нов";
//
//        Student studentToFind = new Student(0, "Алексей", findLastName, 22);
//        Student otherStudent = new Student(0, "Пётр", otherLastName, 25);
//
//        dao.save(studentToFind);
//        dao.save(otherStudent);
//        studentsToClean.add(studentToFind);
//        studentsToClean.add(otherStudent);
//
//        clickOn("#clearButton");
//        Thread.sleep(300);
//
//        String searchText = findLastName.substring(0, findLastName.length() -
//                3);
//        clickOn("#tfFindName").write(searchText);
//
//        clickOn("#findButton");
//        Thread.sleep(500);
//
//        TableView<Student> tableView = lookup("#studentsTable").query();
//
//        boolean foundTarget = false;
//        boolean foundOther = false;
//
//        for (Student s : tableView.getItems()) {
//            if (s.getLastName().equals(findLastName)) {
//                foundTarget = true;
//            }
//            if (s.getLastName().equals(otherLastName)) {
//                foundOther = true;
//            }
//        }
//
//        assertThat(foundTarget).isTrue();
//        assertThat(foundOther).isFalse();
//    }
//
//    @SneakyThrows
//    @Test
//    @DisplayName("Проверка, что в поле 'Возраст' нельзя ввести ничего кроме
//    цифр")
//    void testNegativeAgeInput() {
//        clickOn("#addButton");
//        waitFor(3, TimeUnit.SECONDS, () ->
//                lookup("#tfName").tryQuery().isPresent());
//
//        clickOn("#tfName").write("Иван");
//        Button okButton = lookup("Ok").queryButton();
//        assertThat(okButton.isDisabled()).isFalse();
//
//        clickOn("#tfAge")
//                .press(KeyCode.META)
//                .press(KeyCode.A)
//                .release(KeyCode.A)
//                .release(KeyCode.META)
//                .write("a");
//
//        Thread.sleep(100);
//        assertThat(okButton.isDisabled()).isTrue();
//
//        clickOn("Cancel");
//    }
//
//    private boolean isStudentInTable(String firstName, String lastName, int
//            age) {
//        TableView<Student> tableView = lookup("#studentsTable").query();
//        for (Student s : tableView.getItems()) {
//            if (s.getFirstName().equals(firstName) &&
//                    s.getLastName().equals(lastName) &&
//                    s.getAge() == age) {
//                return true;
//            }
//        }
//        return false;
//    }
//}