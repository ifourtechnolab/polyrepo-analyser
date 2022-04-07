# polyrepo-analyser
Analyse data across multiple repositories

## Feature list

### Iteration-1
- Settings analysis
  - Need to know if all the selected repositories have same branch protection rules. If they are different, what is the differece. 
  - Need to know if all the selected repositories have same webhooks. If they are different, what is the differece. 
  - Need to know if any repository has created and managing a repo level secret. 
- Issue analysis
  - is there any `priority-1` labeled issue which is open since more than x days
  - As a product manager, I need to know how fast are we fixing the issues at various priority levels. How many days or hours on the average are we taking to fix P1, P2 etc priorities.
  - As a product manager, I need to know how issues are spread across different labels. Need to see issues per label with an ability to drill-down.
  - As a product manager, I need to know how many issues are raised by people from community and not the team.
- PR analysis
  - Is that any PR where there is no activity since x days
  - Is there any PR which is not merged since x days
- Dashboard
  - As a user I want to save the analytics that I executed so that I don't have to reapply my filters repeatedly.
  - I also want to pin some of these analytics on the home page after login (dashboard) so that results of these analytics are readily available.
  - As a user, I would also want to mark some of the analytics for trend capturing so that I can see how results are changing over a period of time

### Iteration-2
- Label consistency 
  - are all labels, their description and color same across all repos
- User activity analysis
  - Users committing big chunks of code
  - Users who are reviewers but not reviewing PR for long time
  - Users with maximum PR review backlog
  - Collaborators across all repos: who and how many
- Branches
  - List stale branches across all repositories

## Product Backlog
- ability to update and delete 
- org manager role that can update and delete


## Users

- Github organization manager 
- Github organization team member (phase-1)




