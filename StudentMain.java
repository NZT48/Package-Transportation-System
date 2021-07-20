
import rs.etf.sab.tests.*;
import rs.etf.sab.operations.*;
import student.*;

public class StudentMain {

    public static void main(String[] args) {
        CityOperations cityOperations =  new City();
        DistrictOperations districtOperations = new District();
        CourierOperations courierOperations = new Courier();
        CourierRequestOperation courierRequestOperation =new CourierRequest();
        GeneralOperations generalOperations = new General();
        UserOperations userOperations = new User();
        VehicleOperations vehicleOperations = new Vehicle();
        PackageOperations packageOperations =  new Package();

        TestHandler.createInstance(
                cityOperations,
                courierOperations,
                courierRequestOperation,
                districtOperations,
                generalOperations,
                userOperations,
                vehicleOperations,
                packageOperations);

        TestRunner.runTests();
    }
}
