
package archivobinario;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;

public class EmpleadoManager {

    private RandomAccessFile rcods, remps;

    public EmpleadoManager() {
        try {
            File mf = new File("company");
            mf.mkdir();

            rcods = new RandomAccessFile("company/codigos.emp", "r");
            remps = new RandomAccessFile("company/empleados.emp", "rw");

        } catch (IOException o) {
            System.out.println("No Deberia Pasar Esto!");
        }
    }

    private void initCodes() throws IOException {
        if (rcods.length() == 0) {
            rcods.writeInt(1);
        }

    }

    private int getCode() throws IOException {
        rcods.seek(0);
        int code = rcods.readInt();
        rcods.seek(0);
        rcods.writeInt(code + 1);
        return code;
    }

    public void addEmployee(String name, double salary) throws IOException {

        remps.seek(remps.length());

        int code = getCode();
        remps.writeInt(code);
        remps.writeUTF(name);
        remps.writeDouble(salary);
        remps.writeLong(Calendar.getInstance().getTimeInMillis());
        remps.writeLong(0);
        //Crear el folder del empleado
        createEmployeeFolder(code);
    }

    private String employeeFolder(int code) {
        return "company/empleado" + code;
    }

    private void createEmployeeFolder(int code) throws IOException {

        File edir = new File(employeeFolder(code));
        edir.mkdir();
        //Crear las ventas de el empleado

    }

    private RandomAccessFile SalesFileFor(int code) throws IOException {
        String dirPadre = employeeFolder(code);
        int yearActual = Calendar.getInstance().get(Calendar.YEAR);
        String path = dirPadre + "/ventas" + yearActual + ".emp";

        return new RandomAccessFile(path, "rw");
    }

    /*
    Formato : VentasYEAR.emp
    Double venta
    Bool pagado
     */
    private void createYearSalesFileFor(int code) throws IOException {
        RandomAccessFile rvent = SalesFileFor(code);
        if (rvent.length() == 0) {
            for (int mes = 0; mes < 12; mes++) {
                rvent.writeDouble(0);
                rvent.writeBoolean(false);

            }
        }
    }

    public void employeelist() throws IOException {
        remps.seek(0);
        while (remps.getFilePointer() < remps.length()) {

            int code = remps.readInt();
            String name = remps.readUTF();
            double salary = remps.readDouble();
            Date fecha = new Date(remps.readLong());
            if (remps.readLong() == 0) {
                System.out.println("\n-nCodigo: " + code + "\n-Nombre: " + name + "\n-Salario : Lps" + salary + "\n-Fecha contrato" + fecha);
            }
        }

    }

    private boolean isEmployeeActive(int code) throws IOException {
        remps.seek(0);
        while (remps.getFilePointer() < remps.length()) {

            int codeN = remps.readInt();
            long pos = remps.getFilePointer();

            remps.readUTF();
            remps.skipBytes(16);
            if (remps.readLong() == 0 && codeN == code) {
                remps.seek(pos);
                return true;
            }
        }
        return false;

    }

    public boolean fireEmployee(int code) throws IOException {
        if (isEmployeeActive(code)) {
            String name = remps.readUTF();
            remps.skipBytes(16);
            remps.writeLong(new Date().getTime());

            System.out.println("Despidiendo : " + name);
            return true;
        }

        return false;
    }

}  

