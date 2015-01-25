JSCompile
=========
The smartest tool for packaging a JS project.

Sick of writing RequireJS boiler plate just to get your project compiled? JSCompile has you covered.
Given a directory map for a project that looks like:
<br/>TFA
<br/>&nbsp;|---- models
<br/>&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|---- EvaluationModel.js
<br/>&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|---- PersonModel.js
<br/>&nbsp;|---- views
<br/>&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|---- EvaluationPanel.js
<br/>&nbsp;|---- templates
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|---- EvaluationTemplates.js

and a file (EvaluationPanel.js for instance) that looks like:

    /**
        @import TFA.models.EvaluationModel;
        @import TFA.models.PersonModel;
        @import TFA.templates.EvaluationTemplates;
     */
    function EvaluationPanel(personData){
        this.evaluationData = new EvaluationModel();
        this.personData = personData || new PersonModel();
        this.template = EvaluationTemplates.panel;
    }


Running JSCompile will output an initialization function in a js file of your choice, defining namespace objects
derived from the path structure of your project. It injects your object-files for you to use shorthand:

    function initialize_TFAResources(){
        var TFA = {};
        TFA.models = {};
        TFA.views = {};
        TFA.templates = {};

        TFA.models.PersonModel = (function(){
            . . .
        }());

        TFA.models.EvaluationModel = (function(){
            . . .
        }());

        TFA.templates.EvaluationTemplates = (function(){
            . . .
        }());

        TFA.views.EvaluationPanel = (function(EvaluationModel, PersonModel, EvaluationTemplates){

            function EvaluationPanel(personData){
                this.evaluationData = new EvaluationModel();
                this.personData = personData || new PersonModel();
                this.template = EvaluationTemplates.panel;
            }

            return EvaluationPanel;

        }(TFA.models.EvaluationModel, TFA.models.PersonModel, TFA.templates.EvaluationTemplates);

        . . .

        return TFA;
    }


The process of using components defined in other files is now painless.

API
===
<code>java -jar JSCompile.jar &lt;build directory&gt; &lt;output file name&gt; </code>

Caveats
=======
Some major limitations of this compiler:
* @import annotations actually refer to the filepaths of the resource - JSCompile will not
know about the JavaScript object namespaces you create on your own.
