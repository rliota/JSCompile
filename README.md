JSCompile
=========

JSCompile is a tool for packaging disparate JavaScript files into one consolidated page. It allows you to define resource requirements inside comments with the @import annotation, enabling you to organize the build process declarative dependencies. Additionally, it provides namespace object instantiation in the process of pulling together all of your javascript files together (to match the directories the compiler has traversed.)

Given a directory map for a project of
tfa
|---- model
        |---- Evaluation.js
|---- view
        |---- EvaluationPanel.js
|---- template
        |---- EvaluationTemplates.js

JSCompile will output initialization for namespace objects:
{code}
var TFA = {};
var TFA.model = {};
var TFA.view = {};
var TFA.template = {};
{/code}

Which would enable you to declare objects under namespaces (under the assumption that they would exist at runtime):
{code}
//**
  * @import TFA.model.Evaluation
  */
TFA.view.EvaluationPanel = (function(){
        
        . . .
        
        model: new TFA.model.Evaluation()
}())
{/code}
The above code also declares an @import annotation, which will instruct the compiler to append the file found at tfa/view/Evaluation.js to the head of EvaluationPanel.js' code. 

Some major limitations imposed by this compiler:
* For syntactic clarity, the root source directory is capitalized (tfa => TFA) when creating the directory configured namespaces
* @import annotations actually refer to the filepaths of the resource - JSCompile is not "smart" enough to figure out what objects/functions you have declared and where they reside. (tfa/model/Evaluation.js could contain a definition for VNSNY.model.Clinician = ... and it would import the file regardless, without setting up the namespace VNSNY.model.)

API
===
Argument 0: Source path - the filepath from which to begin traversing and organizing resources.
Argument 1: Destination filename - the file in which the compiled resources will be placed.
