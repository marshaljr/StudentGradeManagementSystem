module com.sms {
    requires javafx.controls;
    requires javafx.fxml;
<<<<<<< HEAD
    requires transitive java.sql;
=======
    requires java.sql;
>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f
    requires org.apache.pdfbox;
    requires org.apache.commons.csv;

    opens com.sms            to javafx.fxml;
    opens com.sms.controller to javafx.fxml;
    opens com.sms.model      to javafx.base;

    exports com.sms;
    exports com.sms.controller;
    exports com.sms.model;
    exports com.sms.dao;
    exports com.sms.util;
    exports com.sms.factory;
}
