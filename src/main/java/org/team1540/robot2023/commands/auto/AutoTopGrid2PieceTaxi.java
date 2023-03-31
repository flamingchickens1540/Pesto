package org.team1540.robot2023.commands.auto;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import org.team1540.robot2023.Constants;
import org.team1540.robot2023.commands.arm.Arm;
import org.team1540.robot2023.commands.arm.PivotCommand;
import org.team1540.robot2023.commands.arm.ResetArmPositionCommand;
import org.team1540.robot2023.commands.arm.SetArmPosition;
import org.team1540.robot2023.commands.drivetrain.Drivetrain;
import org.team1540.robot2023.commands.grabber.GrabberIntakeCommand;
import org.team1540.robot2023.commands.grabber.GrabberOuttakeCommand;
import org.team1540.robot2023.commands.grabber.WheeledGrabber;
import org.team1540.robot2023.utils.AutoCommand;
import org.team1540.robot2023.utils.PolePosition;

import java.util.List;

public class AutoTopGrid2PieceTaxi extends AutoCommand {
    public AutoTopGrid2PieceTaxi(Drivetrain drivetrain, Arm arm, WheeledGrabber intake) {
        List<Command> pathCommands = getPathPlannerDriveCommandGroup(drivetrain, "TopGrid2PieceTaxi");
        addCommands(

                new AutoCube(drivetrain, arm, Constants.Auto.highCube.withPolePosition(PolePosition.CENTER), intake, false),
                Commands.parallel(
                        new GrabberIntakeCommand(intake),
                        Commands.sequence(
                                Commands.parallel(
                            pathCommands.get(0),
                                    Commands.sequence(
                                            new ResetArmPositionCommand(arm),
                                            new PivotCommand(arm, Constants.Auto.armDownBackwards)
                                    )
                                ),
                                Commands.parallel(
                                    Commands.sequence(
//                                        new ResetArmPositionCommand(arm),
                                        new SetArmPosition(arm, Constants.Auto.midCube.approach)
                                    ),
                                    pathCommands.get(1)
                                )
                        )
                ),
                new GrabberOuttakeCommand(intake,1),
                new ResetArmPositionCommand(arm)
//                new AutoGridScore(drivetrain, arm, Constants.Auto.midCube.withPolePosition(PolePosition.CENTER), intake, null, false)

        );
    }


}