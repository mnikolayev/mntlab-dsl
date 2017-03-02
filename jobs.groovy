job('MNTLAB-amatveenko-main-build-job') {
    scm {
      github('MNT-Lab/mntlab-dsl', '$BRANCH_NAME')
    }
  parameters {
    gitParameterDefinition {
            name('BRANCH_NAME')
            type('BRANCH')
            defaultValue('amatveenko')
            selectedValue('DEFAULT')
            branch('')
            description('')
            branchFilter('')
            tagFilter('')
            sortMode('ASCENDING')
            useRepository('')
            quickFilterEnabled(false)
    }
    activeChoiceReactiveParam('BUILDS_TRIGGER') {
            choiceType('CHECKBOX')
            groovyScript {
                script('return ["MNTLAB-amatveenko-child1-build-job", "MNTLAB-amatveenko-child2-build-job", "MNTLAB-amatveenko-child3-build-job", "MNTLAB-amatveenko-child4-build-job"]')
            }

    }
  }
    
  steps {
    downstreamParameterized {
     		trigger('$BUILDS_TRIGGER') {
                  block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('UNSTABLE')
                  }
      			parameters {
  	                  predefinedProp('BRANCH_NAME', '$BRANCH_NAME')
      			}
      	}
 	  }
  }
}


for (i = 1; i <5; i++) {
job('MNTLAB-amatveenko-child' + i + '-build-job') {

    scm {
        github('MNT-Lab/mntlab-dsl', '$BRANCH_NAME')
    }
  parameters {
        activeChoiceReactiveParam('BRANCH_NAME') {
        choiceType('SINGLE_SELECT')
        groovyScript {
        script('["origin/amatveenko", "origin/master"]')
        }
        }
  }

  steps {
        shell('''
          bash script.sh > output.txt
          tar -czvf $BRANCH_NAME_dsl_script.tar.gz jobs.groovy
          ls -lh
          echo $BRANCH_NAME
          ''')
	}
          publishers {
          archiveArtifacts('*_dsl_script.tar.gz')
          }
}
}
