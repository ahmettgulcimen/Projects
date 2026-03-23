import os
# Custom pseudorandom number generator with a random initial seed
class PRNG:
    def __init__(self, seed=None):
        # Use a truly random seed if no seed is provided
        if seed is None:
            seed = int.from_bytes(os.urandom(4), 'big')  # Generate a random 32-bit integer
        self.state = seed

    def randint(self, low, high):
        # Simple linear congruential generator (LCG)
        self.state = (1103515245 * self.state + 12345) % (2**31)
        return low + (self.state % (high - low + 1))

# Generate logistics dataset
def generate_logistics_dataset(num_warehouses=100, max_packages=1000, seed=None):
    """Generates a logistics dataset with a random or specified seed."""
    prng = PRNG(seed)  # Initialize PRNG with the seed or a random one
    data = []
    for i in range(1, num_warehouses + 1):
        warehouse_id = f"WH-{str(i).zfill(3)}"
        priority_level = prng.randint(1, 5)
        package_count = prng.randint(0, max_packages)
        data.append([warehouse_id, priority_level, package_count])
    return data

# Save dataset to a CSV file
def save_to_csv(data, file_name):
    """Saves the dataset to a CSV file."""
    with open(file_name, "w") as file:
        # Write the header
        file.write("Warehouse_ID,Priority_Level,Package_Count\n")
        # Write each row
        for row in data:
            file.write(",".join(map(str, row)) + "\n")


######### YOUR CODE GOES HERE ---  You shoud define here two_level_sorting and the 3 sorting functions

### Your three sorting functions should have global variable named as counter. So do not return it.
counter = 0
def bubble_sort(dataset,step):
    global counter
    n = len(dataset)
    for i in range(n - 1):
        for j in range(n - i - 1):
            counter += 1
            if dataset[j][step] > dataset[j + 1][step]:
                dataset[j], dataset[j + 1] = dataset[j + 1], dataset[j]
    return dataset
def merge_sort(datas_copy, step):
    if len(datas_copy) <= 1:
        return datas_copy[:]
    def merge(left, right, step):
        global counter
        result = []
        while left and right:
            if left[0][step] <= right[0][step]:
                result.append(left.pop(0))
                counter += 1
            else:
                result.append(right.pop(0))
        result.extend(left if left else right)
        return result
    get_mid = lambda lst: len(lst) // 2
    mid = get_mid(datas_copy)
    left = merge_sort(datas_copy[:mid], step)
    right = merge_sort(datas_copy[mid:], step)
    return merge(left, right, step)
def quick_sort(datas_copy, step):
    global counter
    if len(datas_copy) <= 1:
        return datas_copy[:]
    counter += 1
    pivot_index = len(datas_copy) // 2
    pivot = datas_copy[pivot_index]
    less, equal, more = [], [], []
    for data in datas_copy:
        if data[step] < pivot[step]:
            less.append(data)
        elif data[step] == pivot[step]:
            equal.append(data)
        else:
            more.append(data)
    sorted_less = quick_sort(less, step)
    sorted_more = quick_sort(more, step)
    return sorted_less + equal + sorted_more
def two_level_sorting(sort_function, dataset):
    if len(dataset) <= 1:
        return dataset, 0, 0
    global counter
    counter = 0
    processed_data = [row[:] for row in dataset]
    first_level_sorted = sort_function(processed_data, 1)
    first_level_iterations = counter

    counter = 0
    final_sorted = []
    for priority_level in range(1, 6):  # Priority levels are assumed to be from 1 to 5
        group = [row for row in first_level_sorted if row[1] == priority_level]
        final_sorted.extend(sort_function(group, 2))

    second_level_iterations = counter

    return final_sorted, first_level_iterations, second_level_iterations



#########

def write_output_file(
    bubble_sorted, merge_sorted, quick_sorted,
    bubble_sort_pl_iterations, merge_sort_pl_counter, quick_sort_pl_counter,
    bubble_sort_pc_iterations, merge_sort_pc_counter, quick_sort_pc_counter,
    merge_check, quick_check
):
    """Write sorted results and comparisons to the output file."""
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as file:
        file.write("=== Bubble Sorted Results ===\n")
        # file.write(bubble_sorted.to_string() + "\n\n")
        file.write("Warehouse_ID  Priority_Level  Package_Count\n")
        file.write("-" * 40 + "\n")
        for row in bubble_sorted:
            file.write(f"{row[0]:<12}  {row[1]:<14}  {row[2]:<13}\n")
        file.write("\n")
        file.write("=== Comparison Results ===\n")
        if merge_check:
            file.write("Merge and Bubble sorts are identical.\n")
        else:
            file.write("Merge and Bubble sorts differ.\n")
        
        if quick_check:
            file.write("Quick and Bubble sorts are identical.\n")
        else:
            file.write("Quick and Bubble sorts differ.\n")
        
        file.write("\n=== Sort Performance Metrics ===\n")
        file.write(f"Bubble priority sort iteration count: {bubble_sort_pl_iterations}\n")
        file.write(f"Merge priority sort n_of right array is smaller than left: {merge_sort_pl_counter}\n")
        file.write(f"Quick priority sort recursive step count: {quick_sort_pl_counter}\n\n")
        
        file.write(f"Bubble package count sort iteration count: {bubble_sort_pc_iterations}\n")
        file.write(f"Merge package count n_of right array is smaller than left: {merge_sort_pc_counter}\n")
        file.write(f"Quick package count sort recursive step count: {quick_sort_pc_counter}\n")
    
    print(f"Results written to {OUTPUT_FILE}")
    
if __name__ == "__main__":
    # File paths and dataset size
    # Specify paths for input and output files
    INPUT_FILE = "./hw05_input.csv"   # Path where the generated dataset will be saved
    OUTPUT_FILE = "./hw05_output.txt"  # Path where the sorted results and metrics will be saved
    SIZE = 100  # Number of warehouses in the dataset

    # Generate the dataset
    dataset = generate_logistics_dataset(SIZE, max_packages=100)  # Generate a dataset with SIZE warehouses and max_packages packages
    
    # Save the generated dataset to the input file
    save_to_csv(dataset, INPUT_FILE)
    
    
    ###############################################################################################################
    # Perform sorting and counting operations
    # Sort using Bubble Sort and count iterations for Priority Level (_pl_) and Package Count (_pc_)
    bubble_sorted, bubble_sort_pl_iterations, bubble_sort_pc_iterations = two_level_sorting(bubble_sort, dataset)
    
    # Sort using Merge Sort and count recursive steps for Priority Level and Package Count
    merge_sorted, merge_sort_pl_counter, merge_sort_pc_counter = two_level_sorting(merge_sort, dataset)
    
    # Sort using Quick Sort and count recursive steps for Priority Level and Package Count
    quick_sorted, quick_sort_pl_counter, quick_sort_pc_counter = two_level_sorting(quick_sort, dataset)
    ###############################################################################################################
    
    
    # Comparisons
    # Check if Merge Sort results match Bubble Sort results
    merge_check = merge_sorted == bubble_sorted

    # Check if Quick Sort results match Bubble Sort results
    quick_check = quick_sorted == bubble_sorted

    # Write results and metrics to the output file
    write_output_file(
        bubble_sorted, merge_sorted, quick_sorted,
        bubble_sort_pl_iterations, merge_sort_pl_counter, quick_sort_pl_counter,
        bubble_sort_pc_iterations, merge_sort_pc_counter, quick_sort_pc_counter,
        merge_check, quick_check
    )


   
