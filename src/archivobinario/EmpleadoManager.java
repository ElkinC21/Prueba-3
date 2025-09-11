/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

            rcods = new RandomAccessFile("company/codigos.emp", "rw");
            remps = new RandomAccessFile("company/empleados.emp", "rw");
            initCodes();
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
                System.out.println("\n-Codigo: " + code + "\n-Nombre: " + name + "\n-Salario : Lps" + salary + "\n-Fecha contrato " + fecha);
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

  
    public void addSaleToEmployee(int code, double monto, int mesUsuario) throws IOException {
        if (!isEmployeeActive(code)) {
            return;
        }
        int mes = mesUsuario - 1;
        if (mes < 0 || mes > 11) {
            return;
        }
        createYearSalesFileFor(code);
        RandomAccessFile rventa = SalesFileFor(code);
        rventa.seek(mes * 9);
        double ventasMes = rventa.readDouble();
        boolean pagado = rventa.readBoolean();
        rventa.seek(mes * 9);
        rventa.writeDouble(ventasMes + monto);
        rventa.writeBoolean(pagado);
    }

    public void payEmployee(int code) throws IOException {
        if (!isEmployeeActive(code)) {
            return;
        }
        String name = remps.readUTF();
        double salario = remps.readDouble();
        remps.readLong();
        remps.readLong();
        createYearSalesFileFor(code);
        RandomAccessFile rventa = SalesFileFor(code);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int mes = Calendar.getInstance().get(Calendar.MONTH);
        rventa.seek(mes * 9);
        double ventasMes = rventa.readDouble();
        boolean pagado = rventa.readBoolean();
        if (pagado) {
            return;
        }
        double comision = ventasMes * 0.10;
        double sueldo = salario + comision;
        double deduccion = sueldo * 0.035;
        double sueldototal = sueldo - deduccion;
        String recibosPath = employeeFolder(code) + "/recibos.emp";
        RandomAccessFile recibo = new RandomAccessFile(recibosPath, "rw");
        recibo.seek(recibo.length());
        recibo.writeLong(System.currentTimeMillis());
        recibo.writeDouble(comision);
        recibo.writeDouble(sueldo);
        recibo.writeDouble(deduccion);
        recibo.writeDouble(sueldo);
        recibo.writeInt(year);
        recibo.writeInt(mes);
        rventa.seek(mes * 9 + 8);
        rventa.writeBoolean(true);
        System.out.println("Empleado " + name + " se le pago Lps." + sueldototal);
    }

    public void printEmployee(int code) throws IOException {
        remps.seek(0);
        String name = null;
        double salary = 0;
        long contrato = 0;
        while (remps.getFilePointer() < remps.length()) {
            int codeN = remps.readInt();
            String n = remps.readUTF();
            double s = remps.readDouble();
            long fecha = remps.readLong();
            remps.readLong();
            if (codeN == code) {
                name = n;
                salary = s;
                contrato = fecha;
                break;
            }
        }
        if (name == null) {
            System.out.println("Empleado no encontrado");
            return;
        }
        System.out.println("----------Informacion Empleado-----");
        System.out.println("Codigo: " + code);
        System.out.println("Nombre: " + name);
        System.out.println("Salario: Lps " + salary);
        System.out.println("Fecha de contratacion: " + new Date(contrato));
        createYearSalesFileFor(code);
        RandomAccessFile rvent = SalesFileFor(code);
        double totalAnual = 0;
        System.out.println("\nVentas :");
        for (int m = 0; m < 12; m++) {
            rvent.seek(m * 9);
            double venta = rvent.readDouble();
            rvent.readBoolean();
            totalAnual += venta;
            System.out.println("Mes " + (m + 1) + ": Lps " + venta);
        }
        System.out.println("Total anual de ventas: Lps " + totalAnual);
        String recibospath = employeeFolder(code) + "/recibos.emp";
        File file = new File(recibospath);
        long contador = 0;
        if (file.exists()) {
            RandomAccessFile recibo = new RandomAccessFile(recibospath, "r");
            long len = recibo.length();
            contador = len / 48;
        }
        System.out.println("Cantidad total de recibos: " + contador);
    }
}
