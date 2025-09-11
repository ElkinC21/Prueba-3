/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package archivobinario;

import java.io.IOException;
import java.util.Scanner;

public class EmpleadoMain {
    public static void main(String[] args) throws IOException {
        Scanner lea = new Scanner(System.in);
        EmpleadoManager manejo = new EmpleadoManager();
        int opcion = 0;

        while (opcion != 7) {
            System.out.println("\n1.Agregar Empleado");
            System.out.println("2.Listar empleados no despedidos");
            System.out.println("3.Agregar venta a empleado");
            System.out.println("4.Pagar empleado");
            System.out.println("5.Despedir Empleado");
            System.out.println("6.Mostrar informacion");
            System.out.println("7.Salir\n");
            System.out.print("Ingrese opcion: ");

            opcion = lea.nextInt();
            lea.nextLine();

            switch (opcion) {
                case 1: {
                    System.out.print("Ingrese Nombre: ");
                    String nombre = lea.nextLine();
                    System.out.print("Ingrese salario: ");
                    double salario = lea.nextDouble();
                    lea.nextLine();
                    manejo.addEmployee(nombre, salario);
                    System.out.println("Empleado agregado");
                    break;
                }
                case 2: {
                    manejo.employeelist();
                    break;
                }
                case 3: {
                    System.out.print("Codigo del empleado: ");
                    int code = lea.nextInt();
                    lea.nextLine();
                    System.out.print("Monto de la venta: ");
                    double monto = lea.nextDouble();
                    lea.nextLine();
                    System.out.print("Ingrese numero del mes a agregar: ");
                    int mes = lea.nextInt();
                    lea.nextLine();
                    manejo.addSaleToEmployee(code, monto, mes);
                    System.out.println("Venta agregada");
                    break;
                }
                case 4: {
                    System.out.print("Codigo del empleado: ");
                    int code = lea.nextInt();
                    lea.nextLine();
                    manejo.payEmployee(code);
                    break;
                }
                case 5: {
                    System.out.print("Codigo del empleado: ");
                    int code = lea.nextInt();
                    lea.nextLine();
                    if (!manejo.fireEmployee(code)) {
                        System.out.println("No se pudo despedir");
                    }
                    break;
                }
                case 6: {
                    System.out.print("Codigo del empleado: ");
                    int code = lea.nextInt();
                    lea.nextLine();
                    manejo.printEmployee(code);
                    break;
                }
                case 7: {
                    System.out.println("Saliendo...");
                    break;
                }
                default: {
                    System.out.println("Opcion invalida");
                    break;
                }
            }
        }
    }
}
