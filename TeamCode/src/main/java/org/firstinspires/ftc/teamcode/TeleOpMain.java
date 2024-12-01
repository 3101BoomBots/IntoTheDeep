package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@TeleOp(name = "TeleOpMain")
public class TeleOpMain extends LinearOpMode{
    Hardware hw = Hardware.getInstance(this);

    double strafe;
    double drive;
    double turn;

    final double SLOW_POWER = 0.35;
    final int PIVOT_INCREMENT = 120;
    final double INTAKE_MAX_POWER = 1;

    // kD is the constant difference between these positions and the minimum zero point
    final int kD_MAX_PIVOT = 5300;
    final int kD_FACING_UP = 2620;
    final int kD_FACING_DOWN = 2200;
    int kD_MAX_SLIDES_POS = 1675;
    final int kD_TALL_MAX_SLIDES = 2250;
    // little more for safety
    // MAX - 90deg (perpendicular to robot)  = 5633
    // Straight ahead -2500
    // 5163 PIVOT, 1971 Slides high basket
    int minSlidesPos = 0;
    int minPivotPos = 0;
    int slidesIncrement;
    int maxSlidesPos;
    int tallMaxSlides;
    int maxPivotPos;
    int facingUp;
    int facingDown;
    @Override
    public void runOpMode() throws InterruptedException {
        hw.init(hardwareMap);

        waitForStart();

        double intakePower = 0.0;
        while (opModeIsActive()) {
            maxPivotPos = minPivotPos + kD_MAX_PIVOT;
            facingUp = minPivotPos + kD_FACING_UP;
            facingDown = minPivotPos + kD_FACING_DOWN;
            maxSlidesPos = minSlidesPos + kD_MAX_SLIDES_POS;
            tallMaxSlides = minSlidesPos + kD_TALL_MAX_SLIDES;

            if(hw.slidesPushMotor.getCurrentPosition() - 200 <= minSlidesPos) slidesIncrement = 45;
            else slidesIncrement = 150;

            drive = -gamepad1.left_stick_y;
            strafe = gamepad1.left_stick_x;
            turn = gamepad1.right_stick_x;

            boolean dpadUpClicked = gamepad1.dpad_up;
            boolean dpadLeftClicked = gamepad1.dpad_left;
            boolean dpadRightClicked = gamepad1.dpad_right;
            boolean dpadDownClicked = gamepad1.dpad_down;

            if(dpadUpClicked || dpadRightClicked || dpadLeftClicked || dpadDownClicked) resetControls();
            if(dpadUpClicked) drive += SLOW_POWER;
            if(dpadDownClicked) drive -= SLOW_POWER;
            if(dpadLeftClicked) strafe -= SLOW_POWER;
            if(dpadRightClicked) strafe += SLOW_POWER;

            double maxPower = Math.max(Math.abs(drive) + Math.abs(turn) + Math.abs(strafe), 1.0);

            hw.frontLeft.setPower((drive + strafe + turn) / maxPower);
            hw.backLeft.setPower((drive - strafe + turn) / maxPower);
            hw.frontRight.setPower((drive - strafe - turn) / maxPower);
            hw.backRight.setPower((drive + strafe - turn) / maxPower);

            hw.slidesPushMotor.setPower(1);
            hw.slidesPivotMotor.setPower(1);

            double slides = gamepad2.right_stick_y;
            double pivot = gamepad2.left_stick_y;

            hw.slidesPushMotor.setTargetPosition((int) (hw.slidesPushMotor.getTargetPosition() - (slides * slidesIncrement)));
            if (hw.slidesPushMotor.getTargetPosition() < minSlidesPos && !gamepad2.a)
                hw.slidesPushMotor.setTargetPosition(minSlidesPos);
            if(gamepad2.a && hw.slidesPushMotor.getTargetPosition() < 200) minSlidesPos = hw.slidesPushMotor.getCurrentPosition();

            // Not facing straight ahead
            if (hw.slidesPivotMotor.getTargetPosition() > facingUp || hw.slidesPivotMotor.getTargetPosition() < facingDown) {
                if (hw.slidesPushMotor.getTargetPosition() > tallMaxSlides)
                    hw.slidesPushMotor.setTargetPosition(tallMaxSlides);
            } else if (hw.slidesPushMotor.getTargetPosition() > maxSlidesPos && !gamepad2.a)
                hw.slidesPushMotor.setTargetPosition(maxSlidesPos);

            hw.slidesPivotMotor.setTargetPosition((int) (hw.slidesPivotMotor.getTargetPosition() - (pivot * PIVOT_INCREMENT)));
            if (hw.slidesPivotMotor.getTargetPosition() < minPivotPos && !gamepad2.b)
                hw.slidesPivotMotor.setTargetPosition(minPivotPos);
            if (gamepad2.b && hw.slidesPivotMotor.getTargetPosition() < 200) minPivotPos = hw.slidesPivotMotor.getCurrentPosition();
            if (hw.slidesPivotMotor.getTargetPosition() > maxPivotPos && !gamepad2.b)
                hw.slidesPivotMotor.setTargetPosition(maxPivotPos);

            if(gamepad2.dpad_up) intakePower = INTAKE_MAX_POWER;
            if(gamepad2.dpad_down) intakePower = -INTAKE_MAX_POWER;
            if(gamepad2.dpad_left) intakePower = 0;

            hw.intakeServo.setPower(intakePower);

            if(gamepad1.y) {
                hw.hangingMotor.setPower(1);
                drive += 0.4;
            }
            if(gamepad1.x) {
                hw.hangingMotor.setPower(0);
                drive = 0;
            }
            if(gamepad1.a) {
                hw.hangingMotor.setPower(-1);
                drive -= 0.4;
            }

            telemetryMaxValues();
            hw.telemetryHardware();
            telemetry.update();
        }
    }

    private void resetControls() {
        this.drive = 0;
        this.strafe = 0;
        this.turn = 0;
    }

    private void telemetryMaxValues() {
        telemetry.addData("Max Pivot Pos", maxPivotPos);
        telemetry.addData("Facing Up Pos", facingUp);
        telemetry.addData("Facing Down Pos", facingDown);
        telemetry.addData("Max Slides Pos", maxSlidesPos);
        telemetry.addData("Max Tall Slides Pos", tallMaxSlides);
    }
}
