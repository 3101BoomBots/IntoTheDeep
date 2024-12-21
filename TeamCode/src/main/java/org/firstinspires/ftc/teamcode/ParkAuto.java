package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "ParkAuto")
public class ParkAuto extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hw = Hardware.getInstance(this);
        hw.init(hardwareMap);
        Double[] powers = {1.0, -1.0};
        telemetry.addData("", "Press gamepad1 dpad_left to drive straight forwards");
        telemetry.addData("", "Press gamepad1 dpad_right to drive straight backwards");
        telemetry.update();
        double power = getInput(powers);
        telemetry.addData("", "This will literally just move straight one direction, " +
                "be ready to press stop.");
        telemetry.update();
        waitForStart();
        while(opModeIsActive()) {
            hw.setMotorsToPower(power);
            hw.telemetryHardware();
            telemetry.update();
        }
    }

    // any type T
    public <T> T getInput(T[] modes) throws InterruptedException {
        while (opModeInInit()) {
            if (gamepad1.dpad_left && modes[0] != null) {
                sleep(500);
                return modes[0];
            } else if (gamepad1.dpad_right && modes[1] != null) {
                sleep(500);
                return modes[1];
            } else if (gamepad1.dpad_up && modes[2] != null) {
                sleep(500);
                return modes[2];
            } else if (gamepad1.dpad_down && modes[3] != null) {
                sleep(500);
                return modes[3];
            }
            sleep(30);
        }
        throw new InterruptedException("Op Mode not meant to be init or this is not meant to be running.");
    }
}
