student = 'abilun'
job('MNTLAB-' + student + '-main-build-job') {
    scm {
        github('MNT-Lab/mntlab-dsl', '$BRANCH_NAME')
    }
    parameters {
	    gitParameterDefinition{
		    name('BRANCH_NAME')
		    type('BRANCH')
		    defaultValue('abilun')
		    selectedValue('DEFAULT')
		    branch('origin/abilun')
		    description('')
		    branchFilter('')
		    tagFilter('')
		    sortMode('NONE')
		    useRepository('')
		    quickFilterEnabled(false)
	    }
    activeChoiceReactiveParam('BUILD_TRIGGER') {
            choiceType('CHECKBOX')
            groovyScript {
                script('return ["MNTLAB-abilun-child1-build-job", "MNTLAB-abilun-child2-build-job", "MNTLAB-abilun-child3-build-job", "MNTLAB-abilun-child4-build-job"]')
            }
    	}
    }
        
    steps {
        downstreamParameterized {
            trigger('$BUILD_TRIGGER') {
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
	job('MNTLAB-' + student + '-child' + i + '-build-job') {
    	scm {
        	github('MNT-Lab/mntlab-dsl', '$BRANCH_NAME')
        }
        parameters {
            activeChoiceReactiveParam('BRANCH_NAME') {
                    choiceType('SINGLE_SELECT')
                    groovyScript {
                        script('["origin/abilun", "origin/master"]')
                    }
            }
        }
        steps {
            shell('''#!/bin/bash
bash script.sh > output.txt
tar -czvf ${BRANCH_NAME##*/}_dsl_script.tar.gz jobs.groovy script.sh output.txt '''
          	)
        }
        
        publishers {
        	archiveArtifacts('*.tar.gz')
        }
    }
}
