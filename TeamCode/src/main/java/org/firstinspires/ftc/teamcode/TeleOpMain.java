package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@TeleOp(name = "TeleOpMain")
public class TeleOpMain extends LinearOpMode{
    Hardware hw = Hardware.getInstance(this);
    final int SLIDES_INCREMENT = 10;
    final int MAX_SLIDES_POS = 100;
    @Override
    public void runOpMode() throws InterruptedException {
        hw.init(hardwareMap);
        waitForStart();
        hw.setMotorsToPower(0);

        while (opModeIsActive()) {
            double drive = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;

            double maxPower = Math.max(Math.abs(drive) + Math.abs(turn) + Math.abs(strafe), 1);

            hw.frontLeft.setPower(((drive + strafe + turn) / maxPower));
            hw.backLeft.setPower(((drive - strafe + turn) / maxPower));
            hw.frontRight.setPower(((drive - strafe - turn) / maxPower));
            hw.backRight.setPower(((drive + strafe - turn) / maxPower));

//            if (gamepad1.a) {
//                if (hw.slidesMotor.getTargetPosition() < MAX_SLIDES_POS) hw.slidesMotor.setTargetPosition(hw.slidesMotor.getTargetPosition() + SLIDES_INCREMENT);
//            }

            hw.telemetryHardware();
        }
    }
}
