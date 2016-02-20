#ideaHub Frontend

## Environment setup

Install [Node.js](http://www.nodejs.org).

## Development setup

    $ npm run setup

## Generate build files
To change the build directory, please edit "output_path" defined in package.json.

    $ npm run build

## Debugging
To enable debugging, modify build.js file and uncomment the following line:

    //optimize: "none",

## Build Manager

Main npm tasks to manage build:

#### npm run setup
Installs dependencies

#### npm run build
Concatenates and minifies assets before copying them to configured build folder.

#### npm run jade
Compiles all jade files to 

#### f=index.jade npm run jadef
Compiles a single jade file (e.g; index.jade) to html

#### npm run css
Compiles sass files to css