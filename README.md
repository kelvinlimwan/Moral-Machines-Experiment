# Moral Machines Experiment

The idea of Moral Machines is based on the **Trolley Dilemma**, a fictional scenario presenting a decision-maker with a moral dilemma: choosing ''the lesser of two evils''. The scenario entails an autonomous car whose brakes fail at a pedestrian crossing. As it is too late to relinquish control to the car's passengers, the car needs to make a decision based on the facts available about the situation.

Feel free to familiarize yourself with the problem and explore different scenarios on [MIT's Moral Machine Website](https://www.moralmachine.net/).

The **Ethical Engine** algorithm is designed to explore different precarious scenarios and decide between saving either the life of the car's passengers or the life of the pedestrians. Its decision-making is audited through simulations, and it allows users to judge the outcomes themselves.

## Tests
We have made two types of local tests available: 1) an invocation test with some basic assertions and 2) a program input/output test.

### The TestRunner
The file [TestRunner.java](./TestRunner.java) comprises a range of methods to instantiate and invoke the basic functionality of the code. You can run it as follows:

```
javac TestRunner.java && java TestRunnner
```

### Scenario Import and Interactive Mode Test

The [tests folder](./tests/) comprises the following files to test the code locally. 

- [config.csv](tests/config.csv) is the example config file depicted the specification document.
- [config_3.csv](tests/config_3.csv): contains 3 example scenarios and is basis for the interactive mode test described below.
- [config_10.csv](tests/config_10.csv): contains 10 example scenarios.
- [config_100.csv](tests/config_100.csv): contains 100 example scenarios.
- [in_interactive_config_3](tests/in_interactive_config_3): is an example user input file.
- [out_help](tests/out_help): contains the expected output of the <i>help</i> command-line flag.
- [out_interactive_config_3](tests/out_interactive_config_3): contains the expected ouput of the interactive mode test described below.

To test the logic and the expected output in interactive mode, run the program with the following parameters:

```
java EthicalEngine -i -c tests/config_3.csv < tests/in_interactive_config_3 > output
```

This command will run the EthicalEngine main method in interactive mode. It will import three scenarios from the [config_3.csv](./tests/config_3.csv) file and pipe in the pre-defined user input from the [in_interactive_config_3](./tests/in_interactive_config_3).

