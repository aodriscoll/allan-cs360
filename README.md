# CS-360-15309-M01 Mobile Architect & Programming 2024 C-5 (Sep - Oct)

```
 _____  _   _  _   _  _   _   _____  _____          _____   ____  _____ 
/  ___|| \ | || | | || | | | /  __ \/  ___|        |____ | / ___||  _  |
\ `--. |  \| || |_| || | | | | /  \/\ `--.  ______     / // /___ | |/' |
 `--. \| . ` ||  _  || | | | | |     `--. \|______|    \ \| ___ \|  /| |
/\__/ /| |\  || | | || |_| | | \__/\/\__/ /        .___/ /| \_/ |\ |_/ /
\____/ \_| \_/\_| |_/ \___/   \____/\____/         \____/ \_____/ \___/ 
                                                                        
```

## Course Information
|              |                                                                                                                                                                        |
| ------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Institution  | Southern New Hampshire University                                                                                                                                      |
| Course       | [CS-360-15309-M01 Mobile Architect & Programming 2024](https://learn.snhu.edu/d2l/home/1698605 "CS-360-15309-M01 Mobile Architect & Programming 2024 C-5 (Sep - Oct)") |
| Instructor   | **Bill Chan, M.S.** b.chan@snhu.edu                                                                                                                                    |
| GitHub       | btcsnhu                                                                                                                                                                |
| Linked In    |                                                                                                                                                                        |
| Course Dates | 09/02/2024 - 10/27/2024                                                                                                                                                |
| Status       | Active/Online                                                                                                                                                          |

## Artifacts
| Description                           | Link                                                                                                                                                                 |
| :------------------------------------ | :------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Project Three Mobile Application Zip  | [Allan_ODriscoll_CS360_7_2_Project_Three_Inventory_Application_Code_20241020.zip](Allan_ODriscoll_CS360_7_2_Project_Three_Inventory_Application_Code_20241020.zip)   |
| Project Three Mobile Application      | [Allan_ODriscoll_CS360_7_2_Project_Three_Inventory_Application_Code_20241020](Allan_ODriscoll_CS360_7_2_Project_Three_Inventory_Application_Code_20241020)           |
| Project Three Launch Plan             | [Allan_ODriscoll_CS360_7_2_Project_Three_Inventory_Application_20241020.docx](Allan_ODriscoll_CS360_7_2_Project_Three_Inventory_Application_20241020.docx)           |

## 8-3 Journal: Portfolio Submission

### Briefly summarize the requirements and goals of the app you developed. What user needs was this app designed to address?

In project three, I chose to work on a mobile inventory application. The project had several requirements, including the following:

- A database that could store information about users and inventory items.
- A login screen and a way to create new users.
- A screen that displays the inventory items in a grid.
- A mechanism to add and remove items from the inventory.
- A mechanism to adjust the inventory count.
- A mechanism to alert users when the inventory level drops to zero.

I decided to target home users who would use the application to maintain the inventory levels in their pantry and for other household items. The user base would include those who cooked meals, shopped for groceries, engaged in hobbies, and who consumed items from the inventory. I wanted the app to be straightforward and easy to use. One way that I accomplished this is by limiting the need for keyboard input. Once the application has been configured, the inventory is managed by scrolling through the list of items and clicking on buttons.

In addition to the requirements listed above, I also added the following:

- Support for multiple users. Each user has their own inventory items.
- Support for inventory categories. Categories can either be private or shared.

### What screens and features were necessary to support user needs and produce a user-centered UI for the app? How did your UI designs keep users in mind? Why were your designs successful?

The original design included four screens that would support the user's needs.

- A login screen where users could enter their credentials and log in to the application. A button was also provided on the login screen to support the creation of new users.
- A screen where users could fill out a form to create new user accounts.
- An inventory screen allowing users to manage items in the inventory.
    - Items can added by clicking on a category placeholder card. This takes the user to the "Add Inventory Item" screen
    - Items can be removed by long-clicking on an item or category.
    - The inventory count can be adjusted by clicking on the plus or minus buttons on each card.
- A screen to add new inventory items.
- A database where all user and inventory information would be stored.

This design looked like the following:

![Inventory Application Screen Design](./InventoryApplicationScreenDesign.png)

The final deliverable was close to this design, and I believe that it successfully met the project goals. The UI design kept the users in mind by keeping things simple and ensuring that the navigation between screens was intuitive. As mentioned, I also avoided the need for keyboard input where possible. Here are a few screenshots of the completed application.

![Inventory Application](./InventoryApplicationFinal.png)

### How did you approach the process of coding your app? What techniques or strategies did you use? How could those techniques or strategies be applied in the future?

### How did you test to ensure your code was functional? Why is this process important, and what did it reveal?

### Consider the full app design and development process from initial planning to finalization. Where did you have to innovate to overcome a challenge?

### In what specific component of your mobile app were you particularly successful in demonstrating your knowledge, skills, and experience?