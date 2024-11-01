package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.follower.Follower;
import org.firstinspires.ftc.teamcode.pedroPathing.localization.Pose;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.BezierLine;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.Point;

@Autonomous(name="pedroAuto")
public class PedroAuto extends LinearOpMode {
    Follower robot = new Follower(hardwareMap);
    Pose initialPos = new Pose(2, 2);

    @Override
    public void runOpMode() {
        robot.setStartingPose(initialPos);
        waitForStart();
        // test
        if(opModeIsActive()) robot.pathBuilder()
                .addPath(new BezierLine(
                        new Point(new Pose(2, 2)), new Point(new Pose(6, 13))
                )).build();
    }
}
