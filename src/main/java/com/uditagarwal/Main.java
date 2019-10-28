package com.uditagarwal;

import static com.uditagarwal.commands.CommandConstants.EXIT;

import com.uditagarwal.commands.CommandExecutor;
import com.uditagarwal.commands.CommandExecutorFactory;
import com.uditagarwal.exception.InvalidCommandException;
import com.uditagarwal.exception.InvalidModeException;
import com.uditagarwal.model.Command;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
  public static void main(String[] args) throws IOException {
    OutputPrinter.welcome();
    final CommandExecutorFactory commandExecutorFactory = new CommandExecutorFactory();

    if (isInteractiveMode(args)) {
      OutputPrinter.usage();
      while (true) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final String input = reader.readLine();
        final Command command = new Command(input);
        if (command.getCommandName().equals(EXIT)) {
          OutputPrinter.endInteractive();
          break;
        }
        processCommand(commandExecutorFactory, command);
      }

    } else if (isFileInputMode(args)) {
      final String fileName = args[0];
      final File file = new File(fileName);
      final BufferedReader reader;
      try {
        reader = new BufferedReader(new FileReader(file));
      } catch (FileNotFoundException e) {
        OutputPrinter.invalidFile();
        return;
      }

      String input = reader.readLine();
      while (input != null) {
        final Command command = new Command(input);
        processCommand(commandExecutorFactory, command);
      }
    } else {
      throw new InvalidModeException();
    }
  }

  private static void processCommand(CommandExecutorFactory commandExecutorFactory,
      Command command) {
    final CommandExecutor commandExecutor = commandExecutorFactory.getCommandExecutor(command);
    if (commandExecutor.validate(command)) {
      commandExecutor.execute(command);
    } else {
      throw new InvalidCommandException();
    }
  }

  private static boolean isFileInputMode(String[] args) {
    return args.length == 1;
  }

  private static boolean isInteractiveMode(String[] args) {
    return args.length == 0;
  }
}
