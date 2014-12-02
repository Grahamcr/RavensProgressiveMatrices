
1. Introduction
By definition, Raven’s Progressive Matrices provide test takers with a set number of
possible answers to choose from. Therefore, given the set of possible answers is finite, I
based my solution on the Generate and Test approach to solving problems, but created three
different ways of generating test cases based on two different data representation models. As
the main object of the Raven’s Progressive Matrices Test is to evaluate how well test takers
are able to comprehend the changes in relationships objects share as shown in a set of
unique figures it made sense to use data models which easily represent changes. Therefore, I
chose to base my representation of the transition of a Raven’s figure from one state to the
next by using Semantic Networks and Frames.
2. Reasoning
At the highest level, this agent uses three different approaches to solving Raven’s
Progressive Matrices Problems, each of which produces a weighted score suggesting how
likely a given answer is to be correct. A higher weight has been given to those approaches
which most consistently produce the correct answer for the largest number of problem, while
a lower weight is given to approaches which are more applicable to a only a specific subset
of problems. These three scores are then combined and the possible answer with the highest
combined score is selected as the best answer to the problem. This strategy of combining the
three scores was adopted in hopes that one approach might be able to compensate for the
others, in the case they should it have difficulty solving a certain type of Raven’s Matrix. It
was additionally thought that using a combination of approaches to solve a logical problem
would more accurately mimic human problem solving strategies.
2.1 Image Processing
To facilitate image processing the Agent uses the standard Java BufferedImage class
to create a 2D Array of bytes to represent the image. The 2D Array is created by checking
the color of each pixel in the BufferedImage and placing a ‘1’ in the array for a black pixel and
a ‘0’ for a white/nonblack
pixel. Once the Agent has completed the 2D Array, it begins to
process the array as shown in Figures 7 & 8. This process begins by looking for a ‘1’ in a row
in the 2D Array, with the Agent processing the array rows left to right, top to bottom, this is
represented in Figure 7 & 8 by the number ’1’. When the Agent does find a ‘1’ in the 2D Array,
it pauses its search for additional ‘1’ in the 2D Array and starts the process of trying to
determine what shape it has found.
Since the Agent is moving top to bottom, left to right through the 2D Array, the Agent
knows that the first ‘1’ it finds should be the top left corner of the shape. With this knowledge
the Agent can begin to trace around the outside of the shape in order to gain additional insight
into the shape’s properties. The first step the Agent takes is to determine how far to the right
in the same row the 2D Array contains consecutive 1’s, with the last consecutive ‘1’ being the
top right corner of the shape. This step is shown as step ‘2’ in Figures 7 & 8. At this point the
Agent knows where the top right and left corners are and therefore it can determine the width
and the middle point of the top of the shape. Now that the Agent knows the middle, top
coordinate it can begin the next step, which is to trace its way around the shape until it
reaches the bottom middle coordinate of the shape. The Agent traces by finding the next
closest ‘1’, with priority given to coordinates in the following order, as defined by their
relationship to the last identified: right of, below and right of, below, below and left of and
finally left of. Once the agent has found a ‘1’ which has the same xcoordinate
value as the
middle and a reasonable ycoordinate,
the Agent considers the right half of the shape to have
been traced. The tracing step is shown as step ‘3’ in Figures 7 & 8.
During the tracing process the agent is also keeping track of how many times it has to
change directions in order maintain tracing the outside of the shape. This information will help
it determine if the line was curved or runs on a dianangle. Additionally the Agent is also
changing every ‘1’ it encounters to current count of how many objects it has run into. For
example if this is the third shape it has traced in the image then each ‘1’ will be replaced by a
‘3’. This helps the agent to discern between shapes which overlap.
After completing it’s tracing of the right side of the shape, the Agent compares the
ycoordinate
of the bottom middle point to the ycoordinate
of the top middle point, as shown
in Figures 7 & 8 as step ‘4’ in order to calculate the height of the shape. Next the Agent
determines if the shape is filled or not by tracing down from the top middle point for as long as
it can before finding a ‘0’, as shown as step ‘5’ in Figures 7 & 8. If the agent can trace longer
than 10 pixels the shape is considered to be filled. Finally the agent determines the width of
the shape at its middle and bottom using the center line and the same process describe for
step ‘2’ of the figures. This process is shown as step ’7’ in Figures 7 & 8.
This entire mapping process gives the Agent the following information: shape width at
top, middle, bottom, shape is/is not filled, shape is/is not curved, shape’s top right, top left,
bottom right and bottom left coordinates. With this information the Agent can begin to make
an educated guess as to which shape the object is. For example if the height and middle
width are equal, the top and bottom widths are small and the traced line was curved it is most
likely a circle. Another example would be if the top width is very small, the middle width is
greater than the top width and the bottom width is greater than the middle width than the
shape is most likely a triangle which is pointing up.
2.2 Mapping
The agent’s approach to solving Raven’s Progressive Matrices can be broken down
into two paths which both start with the process of mapping objects between figures in the
matrix. In order to draw any conclusions about the transitions a matrix undergoes, the agent
has to be able to determine which objects correspond to one another between the figures
within the matrix. The agent uses a highlevel
Analogical Reasoning approach to compare
the deep similarities between the objects.
The agent then uses a weighted scale to determine which objects are most likely to be
the same object between frames. This process takes into account that scenarios can exist
where the best match for one object could be an even better match for a different object.
Therefore an object’s second, third or even fourth best match might be the same object in a
different figure. Additionally, the agent accounts for the possibility that the object could have
been deleted from one frame to the next and there might not be a match for it in the
subsequent frame.
2.3 Semantic Network
The first of the agent’s three approaches is to use a Semantic Network as a data
representation structure for the differences between to Raven’s Figures. First, the agent
establishes a baseline set of Semantic Networks by calculating the Semantic Network for the
differences which exist between each of the figures within the question prompt. The agent
accomplishes this task by comparing the attributes of each object to the attributes of the same
object in the second figure and capturing any differences it finds between the two. The
collection of these differences for all the objects within a pair of frames makes up the
Semantic Network for their differences (See Figure 1.1 & 1.2). For both a 3x3 and a 2x2
matrix a single collection will represent all of the differences found for an entire row or column
which is complete. In other words the agent will only calculate the differences which exist in
columns/rows which do not include the possible answer.
In order to facilitate the testing step of the process later on, each Semantic Network is
calculated in relationship to a column or row in the 3x3 or 2x2 matrix. When looking at a 2x2
matrix, the agent will calculate the columns and rows in relation to Figure A. For a 3x3 matrix
this is not an option as every column and row which does not contain the possible answer
does not also include Figure A. Therefore, the agent just follows the rule of calculating
differences for any complete column or row. This means the agent will complete this process
for the following pairs of Figures: A&B and A&C for a 2x2 matrix and for ((A&B) & (B&C)),
((D&E) & (E&F)), ((A&D) & (D&G)) and ((B&E) & (E&H)) for a 3x3 matrix.
2.4 Generate & Test Semantic
Networks
Once the agent has established a baseline of Semantic Networks for the figures in the
question prompt is can begin testing to see if any of the possible answers generate similar
Semantic Networks when compared to the question prompt figures. The agent generates
each test case by selecting the next possible answer and creating each of the Semantic
Networks it created in 2.2, except this time the agent calculates the networks in relation to the
possible answer for a 2x2 Matrix and in comparison to the other columns and rows for a 3x3
matrix. For example in a 3x3 Matrix, the agent compares the Semantic Network for the
column (C F
I)
containing the possible answer to the Semantic Network for the other
columns (A D
H)
& (B E
G).
The agent also compares the Semantic Network for the row
(G H
I)
containing the possible answer to the Semantic Networks for the other rows (A B
C)
& ( D E
F).
For the testing phase the agent compares the baseline set of Semantic Networks,
which differences between the Figures within the first two columns and the first two rows in a
3x3 Matrix to the two generated set of Semantic Networks, which represent the differences
between the possible answer and the rest of the Figures in the last row and the last column
respectively (See Figure 6). During this comparison, the agent awards a weighted score to
each of the similarities it finds between the Semantic Networks of the two sets (see Figure
1.3). The sum of the awarded scores represents confidence the agent has that the answer is
going to be correct. Each score is then combined with the score generated from the process
detailed in sections 2.4 2.6;
after which, the highest scoring answer is submitted by the
agent as it’s best answer.
2.5 Frames
The second approach the agent uses to solve Raven’s Progressive Matrices relies on
the Frames data representation. To start, the agent creates a frame for each Raven’s Figure
by extracting everything the agent can find out about the figure and organizing it. As shown in
Figure 2 for Figure A, the agent can create a collection of frames in order to represent the
relationships between objects within a Raven’s Figure. Each of these frames are able to
preserve the relationships between a Figure’s Frames and the objects within it.
2.6 MeansEnd
Analysis
Once the agent has created a collection of frames for each Figure it begins to compare
the frames with the goal of discovering the transition objects take from one frame to a next.
This transition from one frame to the next is represented by yet another Frame as shown in
Figure 3. By comparing each of the figures in the question to one another and creating a
Frame to represent the difference between each of them, as a collection, the agent captures
all of the transformations that exist within all of the columns and rows of the matrix. This
information will not always happen to be completely helpful as the changes don’t always exist
throughout the entire matrix. Therefore, this approach is given less weight than the
Propositional Logic approach in Section 2.7.
2.7 Generate & Test Frames
& MeansEnd
Analysis
Next, the agent can apply each of the transition Frames calculated in 2.5 to Figure A in
the question set with the goal of calculating the most likely answer, as seen in Figure 4 for a
2x2 matrix. Since Frames are not a problem solving solution the agent needs to use some
sort of approach to determine which answer is most similar to the one generated in Figure 4.
Given that the agent has already generated a possible answer and only has a set number of
possible answers to compare it to, it is logical to use the Generate and Test method to
determine which answer is most likely to be correct. The agent then follows a process similar
to the Test phase of the Semantic Network approach to determine which answer is most likely
to be correct by using a weighted comparison. Each of the generated scores from this
process are combined with the Semantic Networks score and the Propositional Logic score
for the same answer. The highest combined score is then selected as the best answer to the
question.
2.8 Propositional Logic
The Frame representations of the Raven’s Figures is used in a second problem
solving approach using Propositional Logic when a 3x3 Matrix is encountered. 3x3 Matrices
are large enough to introduce the possibility that patterns between the Figures which make up
a column or row can exist. This possibility presents an interesting challenge and opportunity
to gather additional insight as to what the correct answer could be if the the agent is able to
recognize the pattern. The agent uses Propositional Logic to compare the filler values of the
Frame used to represent each Figure in a particular column or row.
The basic process that the agent uses to recognize a pattern is to look at the value of
the same filler across each Frame in the two complete columns of the matrix and the two
complete rows in the matrix. Next the agent checks to see if that filler’s value changes in
every Frame in a single row or column and if it changes in a similar way in the rest of the
columns and rows. If it does, the agent assumes that it has found a pattern and stores the
value of the filler it found for each Frame. With this information the agent can look at the
column or row which the potential answer belongs to and determine which filler value in the
pattern that column or row is missing.
Through this determination the agent has learned additional information about the
potential answer it is looking for, it has to have the missing filler value in the pattern it
recognized. The agent will complete this process for every filler value the Frame recognized
as the first Figure in the column or row contains. For each pattern it finds it awards additional
points to that answer which are combined with the points awarded from the other two
methods. The process of looking at Raven’s Figures as Propositional logic statements and
determining how two Figures compare to one another is shown in Figure 5.
3. Observed Weaknesses
The agent faces the most difficulty when it is tasked with solving a problem which
requires the solver to make a logical leap to come up with the answer. For example, the
agent cannot solve 2x2 Question #12 because it is impossible to tell if a circle has rotated or
not. This is an assumption that a human would make after considering all possible answers
and seeing that none of them allow for an unchanged circle to exist. The second type the
agent has a weakness for when solving is problems where objects are added. The agent
does not include any logic to map an object which did not exist before in the possible solution
set to the existing objects the sample problem, which again limits the agent’s ability to
determine how objects have changed within the sample question. As a result, the agent has
less information to use in order to determine which answer follows the same transitions and is
in turn, the correct answer.
The agent additionally has difficulty with Raven’s Figures which contain a large
number of objects in each partition of the Matrix, such as the 3x3 Matrices in questions 17, 18
& 20. The reasoning behind this is most likely two fold. The first challenge for the agent is
determining which objects in a partition within the Matrix map to objects in a different partition
when there are many very similar objects, such as the situation seen in question 18. If the
agent cannot create an accurate mapping then it will be unsuccessful when it attempts to
calculate how an object changes throughout the matrix, as it will not always be comparing like
objects. The second challenge is recognizing patterns that a bit untraditional, such as those
which exist on a dianagle, as seen in question 18 or those which use an attribute which is
hard to quantify, such as “rightof”
or “inside”, as seen in question 20. The agent was not
programed to that level of specificity when determining a pattern.
With the addition of the requirement to process Raven’s Figure images in order to
determine object attributes another important weakness has begun to affect the Agent’s
accuracy. The Agent only has the capability to determine the size, shape and fill of an object.
This limitation on the quantity of attributes diminishes the Agent’s ability to recognize
important differences in other attributes between figures which are required in order to
successfully calculate the correct answer.
4. Suggested Improvements
A substantial improvement which could be made to the agent would be to use
additional MetaReasoning
to determine which of the problem solving methods is most likely
going to result in the most accurate answer. For example, if the agent could retain knowledge
of what makes a problem unique, the process it used to solve it and if that process resulted in
a successful answer then it could use that knowledge to apply the same process to similar
problems. This retention could result the agent being able to construct a map of which
processes are most effective at answering certain types of problems after it has been able to
a set of “learning” problems. Since, our agents are allowed to ask if the correct answer was
submitted and the agent is not penalized for incorrect 2x1 or 2x2 problem answers, it is
feasible this approach could be successfully implemented even in this project.
5. Agent Efficiency
On a standard laptop the agent completes its analysis of over 25 problems in less than
a couple of minutes. However, there are several inefficiencies within the agent design which
could be improved to increase performance. The most prevalent violations occur during the
process of mapping the objects in one Raven’s Figure to those in another. This process not
only repeats the steps required to find the best match for an object, but it also repeats the
search for any other objects in the figure that could be a match. This is done to insure that
each object is only mapped to only one other object and that no competing object had a better
similarity than the object which was selected. Additionally, each approach completes the
matching process based on it’s form of data representation. While this can be a benefit, as
one data representation approach might work better than the other in different cases, it is a
identical task which would not have to be repeated. Finally this bottleneck is dependent on the
number of objects within a figure. Therefore, as the number of objects in a figure rises, so
does the runtime of the agent.
A second improvement which could be made to improve the agent’s performance
would be to incorporate more MetaReasoning
into the process the agent takes to solve a
problem. as suggested in the Suggested Improvements section, MetaReasoning
could be
used to determine which method of solving a problem is most likely to produce the most
accurate result. If best approach is known before solution of the problem begins, then there
isn’t has much of a benefit to solving the problem using multiple approaches. Therefore, this
would limit the number of times that the agent has to solve the problem to one and greatly
decrease processing time per problem.
6. Relationship to Human Cognition
The design of this agent proves that sometimes using multiple approaches to solving a
problem can prove to be beneficial as one approach will perform better in certain situations
where it others might be produce as accurate of results. This approach is similar to what
humans do when solving complex logic problems. It is common that we will apply different
strategies that we have learned to a problem and then use the collection of information we
have gathered from the application of each strategy to derive our conclusion. We often refer
to this approach as “exploring” the different options we have to solve a problem. More
typically, humans will only consider each approach which they are aware of to solve a
problem, determine the best approach and solve the problem that way. This is more similar to
the approach which was suggested in the Suggested Improvements section that would rely
more heavily on MetaReasoning
to determine the best strategy for solving a given problem.
The agent further mimics human cognition by using Propositional Logic for pattern
recognition. The easiest way to solve a Raven’s Matrix is to discover the pattern(s) that is
follows and then find the answer which completes the pattern. The agent uses Propositional
Logic to follow this same approach by representing each object and it’s attributes as logical
statements and comparing the statements of figures in the same column or row to find any
patterns in the differences between logic statements. The agent then finds the answer which
completes the pattern. While this process is much more formal and specific than we as
humans would typically think, it does follow the same process.

7. Referenced Figures
Figure 1:
Figure 2:
Figure 3:
Figure 4:
Figure 5:
Figure 6:
Each comparison generates an integer similarity score, which combined give the overall
similarity the possible answer’s differences within it’s own column and rows have to the
question prompt figures within their own columns/rows.
KEY:
Figure 7:
Figure 8:
