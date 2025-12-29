# File System Interface Lecture Notes

## Overview
A file is the logical storage unit.
The file system is the most visible aspect of a general-purpose OS and consists of two parts:
1. **Collection of files** - the logical storage units
2. **Directory structure** - organizes all the files

**Key Goals:**
- The OS map files onto physical storage devices
- The file system must be designed for efficient access (storage is often slow)
- OS provides support for file sharing


## File Concept

### Definition
A **file** is a named collection of related information recorded on secondary storage.
- Logical storage unit provided by OS
- Provides uniform logical view across different storage media (HDD, tape, DVD, etc.)
- Data cannot be written to storage unless within a file
- Files represent both programs and data
- OS maps files onto physical devices

---

## File Attributes

Information kept in directory structure:

1. **Name** - human-readable form (characters)
2. **Identifier** - unique number for the system
3. **Type** - if OS supports different file types
4. **Location** - pointer to device and location on device
5. **Size** - current size and possibly maximum allowed
6. **Protection** - who can read, write, execute
7. **Timestamps** - creation, last change, last use (security/monitoring)
8. **User identification** - who owns/created the file

**Directory Entry Structure:**
- Contains file's name and identifier
- Identifier locates all other attributes

---

## File Operations

### Seven Basic Operations

#### 1. Create a File
- Find space in file system
- Create new entry in directory

#### 2. Open a File
- Returns file handle for subsequent operations
- **All operations (except create/delete) require open() first**
- Accepts access mode: create, read-only, read-write, append-only
- Mode checked against file's permissions

#### 3. Write a File
- Specify handle and content to write
- OS maintains write pointer for next write location

#### 4. Read a File
- Specify handle and destination for content
- OS maintains read pointer for next read location

#### 5. Reposition (Seek)
- Move current-file-position pointer within file

#### 6. Delete a File
- Search for file
- Release file space
- Erase directory entry

#### 7. Truncate a File
- Erase contents but keep attributes (except size)

### Open-File Tables

OS maintains **two levels** of internal tables:

#### Per-Process Table
- Tracks files each process has open
- Stores current-position pointer (unique per process)

#### System-Wide Table
- Process-independent information:
  - Location on disk
  - Size
  - Access rights
- **Open count** - tracks how many processes have file open
  - Entry removed when count reaches 0

### Information Associated with Open File

1. **File pointer** - current position (unique per process)
2. **File-open count** - for table management
3. **File location** - avoids reading directory repeatedly
4. **Access rights** - for permission checking

### File Locks

Useful when files are shared among processes (e.g., log files).

**Types of Locks:**

1. **Shared lock** - multiple processes can hold concurrently (reader lock)
2. **Exclusive lock** - only one process at a time (writer lock)
   - Some OS provide only this type

**Lock Implementation:**

- **Mandatory** - OS enforces, prevents access to locked file (Windows)
- **Advisory** - programs must acquire locks before access (UNIX)

⚠️ **Warning:** Avoid deadlocks when acquiring multiple file locks (prevent circular wait or hold-and-wait)

---

## File Types

### Type Recognition Methods

#### 1. File Extensions (Common)
- Name split into: `name.extension`
- Examples: `Deadlocks.pptx`, `FifoSchedulerImpl.java`
- Extensions are **hints** to applications
- OS doesn't restrict operations based on extension

#### 2. Magic Numbers (UNIX)
Special bytes at file beginning to identify type:
- PDF files: `%PDF` (hex 25 50 44 46)
- JPEG files: hex FF D8
- GIF files: `GIF89a` (hex 47 49 46 38 39 61)
- Scripts: `#!` (hex 23 21)

Not all files have magic numbers - they aid users, not mandatory.

---

## File Structure

### OS Support Approaches

**Minimal Support (UNIX approach):**
- File is just a sequence of bytes
- Maximum flexibility for users
- Minimum support from OS
- All OS must support at least executable files (for loading/running)

**Multiple Type Support:**
- **Disadvantages:**
  - OS becomes larger
  - All files must fit supported types
  - Example problem: Encrypting a text file creates neither text nor executable

### Internal File Structure

- Disk I/O performed in fixed-size blocks
- In UNIX, each byte individually addressable
- Logical record size = 1 byte
- **Internal fragmentation** - last physical block partially wasted
  - Larger block size → greater fragmentation

---

## Access Methods

### 1. Sequential Access
**Simplest and most common** - based on tape model

**Operations:**
- **Read** - reads next portion, advances pointer
- **Write** - appends to end of file
- Process records one after another

### 2. Direct Access
Based on disk model - uses fixed-length logical records

**Characteristics:**
- File viewed as numbered sequence of blocks/records
- Read/write in any order (specify block number)
- **Relative block numbers** - 0 to N
- OS decides physical location
- For block n and length L: OS issues I/O of L bytes starting at L×n

**Use Case:** Databases - compute block containing data, then access

⚠️ **Note:** Not all OS support both methods

### 3. Index-Based Access

- Construct index with file contents
- Search index first to find record location
- Reduces I/O for large file searches
- Large index → create index of index (hierarchical, like page tables)

---

## Directory Structures

### Purpose
Translate file names into file control blocks.

### Required Operations

1. **Search** - find entry for particular file
2. **Create** - add new file to directory
3. **Delete** - remove element from directory
4. **List** - show files and their attributes
5. **Rename** - change file name (may change position)
6. **Traverse** - access every directory and file (backup, search)

---

## Directory Organization Types

### 1. Single-Level Directory

**Simplest approach** - all files in one directory

**Problems:**
- All files must have unique names (scales poorly)
- No user isolation - everybody sees everything

### 2. Two-Level Directory

**Solution:** Separate directory for each user

**Structure:**
- Each user has User File Directory (UFD)
- Search only user's own UFD
- Cannot delete other users' files
- Solves naming conflicts between users

**Path Format:**
- Windows: `C:\usera\file1`
- UNIX: `/u/usera/file1`

**Challenge:** System programs all users need

**Solutions:**
1. ❌ Copy system files to each UFD (wasteful)
2. ✅ Special directory for system files
   - Search UFD first
   - If not found, search special directory
   - Called **search path** (can contain unlimited directories)
   - View with: `echo $PATH`

---

### 3. Tree-Structured Directories

**Natural generalization** - arbitrary number of levels

**Features:**
- Users create their own subdirectories
- Directory is a special type of file (bit distinguishes it)
- Each process has **current directory**

**Path Types:**
- **Absolute path** - from root (e.g., `/home/user/file.txt`)
- **Relative path** - from current directory (e.g., `../file.txt`)

**Deletion Policy:**

**Option 1:** Only allow deletion of empty directories

**Option 2:** Recursive deletion of all subdirectories and files
- Convenient but **dangerous!**
- ⚠️ **DO NOT try:** `rm -rf /*`

---

### 4. Acyclic-Graph Directories

**Purpose:** Share subdirectories/files between users

**Key Feature:** Node can have multiple parents, but no cycles back to itself

**Implementation:**
- Shared entry is a **link** (pointer to another file/subdirectory)
- Link includes path to real file
- Shared file is NOT a copy - changes appear everywhere

**Complications:**

**1. Multiple Paths**
- File may have multiple absolute paths
- Distinct names refer to same file

**2. Traversal Challenges**
- Each file should be handled only once (e.g., during backup)

**3. Deletion Problem**

**Option 1:** Remove immediately when any user deletes
- Leaves **dangling pointers** in other links
- Costly to handle during access

**Option 2:** Remove only when all references deleted
- Keep list of all references? (large, variable size)
- ✅ **Keep reference counter** (stored in file inode)
  - Remove file when counter reaches 0
  - Called **hard links**

---

### 5. General Graph Directory

**Characteristic:** Cycles allowed (difficult to prevent)

**Problems:**

**1. Traversal**
- May enter infinite loop
- Need cycle detection

**2. Deletion**
- Reference count > 0 doesn't guarantee accessibility
- Disconnected cycles can exist after link removal

**Solution: Garbage Collection**

**Process:**
1. Traverse filesystem, marking everything accessible
2. Traverse all files, remove unmarked ones

⚠️ **Drawback:** Extremely time-consuming

---

## Protection

### Goals

#### 1. Reliability
Keep information safe from physical damage
- **Hardware problems:** dirt, temperature, electrical issues
- **Solution:** Redundancy (backup to tape or other media)

#### 2. Protection
Avoid improper access
- User/password authentication
- Firewalls
- Encrypt secondary storage (prevents physical access threats)
- Limit file access types

### Types of Access Operations

1. **Read** - read from file
2. **Write** - write or rewrite file
3. **Execute** - load file into memory and execute
4. **Append** - add information only at end
5. **Delete** - delete file and free space
6. **List** - list name and attributes
7. **Attribute change** - modify file attributes

---

## Access Control Methods

### 1. Access Control Lists (ACL)

**Concept:** Associate each file/directory with list of users and their permissions

**Problems:**
- ACL length (what if everyone can read?)
- Tedious construction
- Variable size directory entries

### 2. User Classifications (UNIX Approach)

**Three categories:**

1. **Owner** - user who created file
   - Ownership can be transferred
2. **Group** - set of users sharing file with similar access
   - Carefully controlled
   - Created/modified only by system administrators
3. **Other** - rest of users in system

### UNIX Permission System

**Three permission types for each category:**
- **r** (read)
- **w** (write)  
- **x** (execute)

**Total:** 3 categories × 3 permissions = **9 bits per file**

**Example:** `rwxr-xr--`
- Owner: read, write, execute
- Group: read, execute
- Other: read only

**Additional:** setuid bit (e.g., for `passwd` command)

### Directory Permissions (UNIX)

- **r** - can list directory contents (`ls`)
- **w** - can create new files/folders
- **x** - can change into directory (`cd`)

---

## Other Protection Approaches

### 1. Password Per File
- Associate password with each file
- **Problem:** Users must remember many passwords
- **Variation:** Single password per subdirectory

### 2. Directory Operation Control
- Control who can:
  - Check if file exists
  - List directory contents
  - Create/delete in directory

---

## Key Takeaways

1. **Files are the fundamental logical storage unit** - all data must be in files
2. **Open-file tables manage shared access** - two levels (per-process and system-wide)
3. **File types help but aren't restrictive** - extensions are hints, not enforcements
4. **Directory structures evolved for sharing** - single → two-level → tree → acyclic graph → general graph
5. **Each structure has tradeoffs:**
   - Trees: simple but no sharing
   - Acyclic graphs: sharing with complexity
   - General graphs: maximum flexibility, maximum complexity
6. **Protection balances security and usability** - UNIX 9-bit system is practical compromise
7. **Cycles create problems** - deletion and traversal become challenging
8. **Locks prevent conflicts** - mandatory (Windows) vs advisory (UNIX)

---

## Important Commands/Concepts

- `echo $PATH` - view search path
- `rm -rf` - recursive forced deletion (dangerous!)
- `/proc/pid/` - process information in UNIX
- File permissions format: `rwxrwxrwx` (owner, group, other)
- Magic numbers - file type indicators
- Hard links - multiple directory entries for same file
- Inode - stores file metadata including reference count