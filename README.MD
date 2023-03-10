Here's a README that presents the project as a complete, finalized compiler for the Toorla language, without mentioning phases.

---

# Toorla Compiler for Semantic and Syntax Analysis

**Description:**  
This repository contains a fully implemented compiler for the Toorla programming language, focusing on both syntax and semantic analysis. The compiler parses Toorla code, generates symbol tables, manages scopes, and provides comprehensive error detection for semantic issues. Using **ANTLR** for parsing and custom-built symbol table and error handling systems, this compiler provides a clear framework for understanding Toorla's program structure and identifying coding errors.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Installation](#installation)
- [Usage](#usage)
- [Features](#features)
- [Project Structure](#project-structure)
- [Implementation Details](#implementation-details)
- [Contributing](#contributing)
- [License](#license)

---

## Project Overview

The Toorla Compiler is designed to perform three core tasks:

1. **Syntax Analysis**: Parsing Toorla code and constructing a syntax tree based on its grammar.
2. **Symbol Table Construction**: Building symbol tables to track identifiers (classes, methods, variables) in their appropriate scopes.
3. **Semantic Analysis**: Detecting and reporting semantic errors, such as duplicate definitions, invalid inheritance structures, and type mismatches.

Through these capabilities, this compiler ensures Toorla code correctness and adherence to language rules, providing detailed feedback for invalid constructs.

---

## Features

### Syntax Analysis

- **ANTLR-Based Parsing**: The compiler uses ANTLR to parse Toorla source code and generate an abstract syntax tree, ensuring syntax validity and preparing for deeper analysis.

### Symbol Table Management

- **Hierarchical Symbol Tables**: Tracks identifiers across nested scopes, from classes and methods to block-level variables, allowing for precise scope management.
- **Insert and Lookup Functions**: Efficient symbol management using `insert` and `lookup` functions, supporting quick addition and retrieval of identifiers.

### Semantic Analysis

- **Error Detection**: Comprehensive checking for issues like duplicate class, method, and variable definitions, as well as inheritance and type mismatches.
- **Detailed Error Reporting**: Pinpoints exact lines and columns for each error, providing useful feedback to improve code quality.
  - Errors detected include:
    - Redefinition of classes, methods, or variables.
    - Cyclic inheritance and incorrect type access.
    - Access violations for private methods.

## Project Structure

- **toorla_compiler.c**: The main file orchestrating the compiler's processes, including parsing, symbol table management, and error detection.
- **symbol_table.c**: Manages the hierarchical structure of symbol tables, handling insertion, lookup, and scope tracking.
- **error_handler.c**: Manages semantic error detection and reporting.
- **antlr**: Contains the ANTLR grammar files used for parsing Toorla code and generating the initial syntax tree.

## Implementation Details

- **Symbol Table Construction**: Implemented as a hash table to manage unique identifiers across various scopes. Key methods include:
  - `insert(idefName, attributes)`: Inserts an identifier with its attributes.
  - `lookup(idefName)`: Retrieves an identifier's attributes, if present.
- **Scope Management**: Each scope level (classes, methods, blocks) is managed with a separate symbol table, creating a hierarchical structure to control visibility and resolve identifiers.
- **Error Detection**: Implements various semantic checks for:
  - Duplicate definitions across scopes.
  - Inheritance loops and return type mismatches.
  - Invalid access to private members across classes.

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a new branch (`git checkout -b feature-branch`)
3. Make your changes
4. Push to the branch (`git push origin feature-branch`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

---

This README provides a complete overview of the Toorla compiler, highlighting its features and usage, making it suitable for a GitHub repository. Let me know if you’d like to adjust anything further!
